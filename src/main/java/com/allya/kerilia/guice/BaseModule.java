package com.allya.kerilia.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public abstract class BaseModule extends AbstractModule
{
    public void start(final Injector injector) throws Exception
    {
    }

    public void stop(final Injector injector) throws Exception
    {
    }
}
