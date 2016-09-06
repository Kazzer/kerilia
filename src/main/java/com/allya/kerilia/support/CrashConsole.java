package com.allya.kerilia.support;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.crsh.standalone.Bootstrap;
import org.crsh.vfs.Path;

import com.allya.kerilia.daemon.Daemon;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class CrashConsole implements Daemon
{
    private final Bootstrap bootstrap;

    @Inject
    public CrashConsole(final Injector injector, @Named("crash.telnet.port") final int telnetPort)
        throws IOException, URISyntaxException
    {
        Validate.isTrue(telnetPort > 0, "telnetPort should be a positive non-null integer");

        bootstrap = new Bootstrap(CrashConsole.class.getClassLoader());

        final Properties configuration = new Properties();
        configuration.setProperty("crash.telnet.port", Integer.toString(telnetPort));
        bootstrap.setConfig(configuration);

        final Map<String, Object> attributes = new HashMap<>();
        for (final Map.Entry<Class<?>, String> entry : getSingletonAttributes().entrySet())
        {
            final Class<?> singletonClass = entry.getKey();
            final String attributeName = entry.getValue();
            final Object singleton = injector.getInstance(singletonClass);
            attributes.put(attributeName, singleton);
        }
        bootstrap.setAttributes(attributes);

        bootstrap.addToCmdPath(Path.get("/crash/commands/"));
    }

    protected Map<Class<?>, String> getSingletonAttributes()
    {
        final Map<Class<?>, String> attributes = new HashMap<>();

        return attributes;
    }

    @Override
    public void start()
    {
        try
        {
            bootstrap.bootstrap();
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop()
    {
        bootstrap.shutdown();
    }
}
