package com.allya.kerilia.guice;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import javax.management.MBeanServer;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public abstract class MainModule extends BaseModule {
    private final Properties properties;

    public MainModule(final Properties properties) {
        this.properties = properties;
        checkPrerequisites();
    }

    protected abstract void checkPrerequisites();

    @Override
    protected void configure() {
        Names.bindProperties(binder(), properties);
    }

    protected Properties getProperties() {
        return properties;
    }

    @Provides
    @Singleton
    @Inject
    MBeanServer provideMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
