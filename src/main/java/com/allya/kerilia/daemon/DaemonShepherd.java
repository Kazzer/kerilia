package com.allya.kerilia.daemon;

import static com.allya.kerilia.exception.StreamableExceptionUtils.uncheck;
import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.name.Names;
import com.j256.simplejmx.common.JmxFolderName;
import com.j256.simplejmx.common.JmxSelfNaming;

public class DaemonShepherd implements JmxSelfNaming {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaemonShepherd.class);

    private final List<Daemon> daemons;

    public DaemonShepherd(final Daemon... daemons) {
        this.daemons = Stream
            .of(daemons)
            .map(daemon -> Validate.notNull(daemon, "Daemons cannot be null"))
            .collect(toCollection(ArrayList::new));
    }

    public void start() {
        LOGGER.info("Starting application...");
        daemons.forEach(daemon -> uncheck(() -> {
            LOGGER.info("Starting {}", daemon.getClass().getSimpleName());
            daemon.start();
        }));
        LOGGER.info("Started all daemons");
    }

    public void stop() {
        LOGGER.info("Shutting down application...");
        Lists.reverse(daemons).forEach(daemon -> uncheck(() -> {
            LOGGER.info("Stopping {}", daemon.getClass().getSimpleName());
            daemon.stop();
        }));
        LOGGER.info("Application has been stopped");
    }

    @Override
    public String getJmxDomainName() {
        return Names.named("jmx.domain.name").value();
    }

    @Override
    public String getJmxBeanName() {
        return "DaemonShepherd";
    }

    @Override
    public JmxFolderName[] getJmxFolderNames() {
        return null;
    }
}
