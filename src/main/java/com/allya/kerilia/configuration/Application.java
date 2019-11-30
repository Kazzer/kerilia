package com.allya.kerilia.configuration;

import static com.allya.kerilia.exception.StreamableExceptionUtils.uncheck;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allya.kerilia.daemon.DaemonShepherd;
import com.allya.kerilia.guice.BaseModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class Application {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Class<? extends DaemonShepherd> daemonShepherdClass;

    private DaemonShepherd daemonShepherd;
    private Injector injector;

    protected Application(final Class<? extends DaemonShepherd> daemonShepherdClass) {
        this.daemonShepherdClass = daemonShepherdClass;
    }

    protected abstract List<Module> getModules();

    protected Injector createInjector(final List<Module> modules) {
        return Guice.createInjector(modules);
    }

    public void start() {
        try {
            final List<Module> modules = getModules();
            Runtime.getRuntime().addShutdownHook(new Thread("shutdown-hook") {
                @Override
                public void run() {
                    try {
                        logger.info("Shutting down");
                        Application.this.stop();
                    }
                    catch (final Throwable t) {
                        logger.error("Error during shutdown", t);
                    }
                }
            });

            injector = createInjector(modules);
            daemonShepherd = injector.getInstance(daemonShepherdClass);

            modules.forEach(module -> uncheck(() -> {
                logger.info("Starting {}", module.getClass().getSimpleName());
                if (module instanceof BaseModule) {
                    ((BaseModule) module).start(injector);
                }
            }));

            daemonShepherd.start();
        }
        catch (final Exception e) {
            throw new RuntimeException("Error during start-up", e);
        }
    }

    public void stop() {
        if (daemonShepherd != null) {
            daemonShepherd.stop();
        }
    }
}
