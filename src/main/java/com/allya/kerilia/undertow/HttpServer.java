package com.allya.kerilia.undertow;

import static com.allya.kerilia.Constants.PORT_MAX;
import static com.allya.kerilia.Constants.PORT_MIN;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allya.kerilia.daemon.Daemon;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

@Singleton
public class HttpServer implements Daemon {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private final String httpHost;
    private final int httpPort;
    private final PathHandler pathHandler;

    private Undertow server;

    @Inject
    public HttpServer(@Named("http.server.host") final String httpHost,
                      @Named("http.server.port") final int httpPort,
                      final PathHandler pathHandler) {
        this.httpHost = Validate.notNull(httpHost, "httpHost cannot be null");
        Validate.exclusiveBetween(PORT_MIN, PORT_MAX, httpPort, "httpPort must be a valid port number");
        this.httpPort = httpPort;
        this.pathHandler = Validate.notNull(pathHandler, "pathHandler cannot be null");
    }

    @Override
    public void start() {
        server = Undertow.builder().addHttpListener(httpPort, httpHost).setHandler(pathHandler).build();
        server.start();

        LOGGER.info("Listening on port: {}", httpPort);
    }

    @Override
    public void stop() {
        server.stop();

        LOGGER.info("Stopped listening on port: {}", httpPort);
    }
}
