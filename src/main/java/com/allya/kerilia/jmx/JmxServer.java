package com.allya.kerilia.jmx;

import static com.allya.kerilia.Constants.PORT_MAX;
import static com.allya.kerilia.Constants.PORT_MIN;

import java.util.ArrayList;
import java.util.List;

import javax.management.JMException;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allya.kerilia.daemon.Daemon;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class JmxServer implements Daemon {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxServer.class);

    private final com.j256.simplejmx.server.JmxServer server;
    private final List<Object> registeredObjects = new ArrayList<>();

    @Inject
    public JmxServer(@Named("jmx.registry.port") final int registryPort,
                     @Named("jmx.server.port") final int serverPort) {
        Validate.exclusiveBetween(PORT_MIN, PORT_MAX, registryPort, "registryPort must be a valid port number");
        Validate.exclusiveBetween(PORT_MIN, PORT_MAX, serverPort, "serverPort must be a valid port number");

        server = new com.j256.simplejmx.server.JmxServer();
        server.setRegistryPort(registryPort);
        server.setServerPort(serverPort);
    }

    @Override
    public void start() throws Exception {
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
        registeredObjects.forEach(this::unregisterMBean);
    }

    public void registerMBean(final Object o) {
        try {
            server.register(o);
            synchronized (registeredObjects) {
                registeredObjects.add(o);
            }
        }
        catch (final JMException jme) {
            LOGGER.warn("Failed to register MBean for: {}", o, jme);
        }
    }

    public void unregisterMBean(final Object o) {
        try {
            synchronized (registeredObjects) {
                registeredObjects.remove(o);
            }
            server.unregisterThrow(o);
        }
        catch (final JMException jme) {
            LOGGER.warn("Failed to unregister MBean for: {}", o, jme);
        }
    }
}
