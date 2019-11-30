package com.allya.kerilia.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allya.kerilia.guice.BaseModule;
import com.allya.kerilia.guice.IsSingletonBindingScopingVisitor;
import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.spi.BindingScopingVisitor;
import com.j256.simplejmx.common.JmxResource;
import com.j256.simplejmx.common.JmxSelfNaming;

public class JmxModule extends BaseModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxModule.class);

    @Override
    protected void configure() {
    }

    @Override
    public void start(final Injector injector) throws Exception {
        super.start(injector);
        final JmxServer jmxServer = injector.getInstance(JmxServer.class);
        jmxServer.start();

        final BindingScopingVisitor<Boolean> scopingVisitor = new IsSingletonBindingScopingVisitor();
        final Predicate<Object> predicate = new JmxInstancePredicate();
        injector.getAllBindings().forEach((key, binding) -> {
            if (!binding.acceptScopingVisitor(scopingVisitor)) {
                return;
            }

            final Object instance = injector.getInstance(key);

            if (instance != null && predicate.apply(instance)) {
                LOGGER.info("Registering " + instance.getClass().getSimpleName() + " with JMX server");
                jmxServer.registerMBean(instance);
            }
        });
    }

    private static class JmxInstancePredicate implements Predicate<Object> {
        @Override
        public boolean apply(final Object instance) {
            return instance != null
                && (instance.getClass().isAnnotationPresent(JmxResource.class) || instance instanceof JmxSelfNaming);
        }
    }
}
