package com.allya.kerilia.daemon;

public interface Daemon
{
    void start() throws Exception;

    void stop() throws Exception;
}
