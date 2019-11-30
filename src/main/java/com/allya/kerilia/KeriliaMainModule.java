package com.allya.kerilia;

import com.allya.kerilia.configuration.ConfigParser;
import com.allya.kerilia.event.handlers.NotFoundHandler;
import com.allya.kerilia.guice.MainModule;
import com.allya.kerilia.undertow.HttpServer;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import io.undertow.server.handlers.PathHandler;

public class KeriliaMainModule extends MainModule {
    public KeriliaMainModule() {
        super(ConfigParser.getProperties(Constants.APP_NAME));
    }

    @Override
    protected void checkPrerequisites() {
        // nothing to do yet
    }

    @Override
    protected void configure() {
        super.configure();
        binder().disableCircularProxies();

        bind(String.class)
            .annotatedWith(Names.named("jmx.domain.name"))
            .toInstance(Constants.JMX_DOMAIN_NAME);

        bind(HttpServer.class).in(Singleton.class);

        bind(NotFoundHandler.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Inject
    PathHandler providePathHandler(final NotFoundHandler notFoundHandler) {
        return new PathHandler(notFoundHandler);
    }
}
