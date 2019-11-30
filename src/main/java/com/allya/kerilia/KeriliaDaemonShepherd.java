package com.allya.kerilia;

import com.allya.kerilia.daemon.DaemonShepherd;
import com.allya.kerilia.support.CrashConsole;
import com.allya.kerilia.undertow.HttpServer;
import com.google.inject.Inject;

public class KeriliaDaemonShepherd extends DaemonShepherd {
    @Inject
    public KeriliaDaemonShepherd(final CrashConsole crashConsole,
                                 final HttpServer httpServer) {
        super(crashConsole, httpServer);
    }
}
