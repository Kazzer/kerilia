package com.allya.kerilia;

import java.util.List;

import com.allya.kerilia.configuration.Application;
import com.allya.kerilia.jmx.JmxModule;
import com.google.common.collect.Lists;
import com.google.inject.Module;

public class KeriliaApp extends Application {
    protected KeriliaApp() {
        super(KeriliaDaemonShepherd.class);
    }

    public static void main(final String[] args) throws Exception {
        final KeriliaApp app = new KeriliaApp();
        try {
            app.start();
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    protected List<Module> getModules() {
        return Lists.<Module>newArrayList(new KeriliaMainModule(), new JmxModule());
    }
}
