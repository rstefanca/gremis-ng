package cz.upce.gremis.gremisng.core;

import org.springframework.context.ApplicationContext;


public final class ContextWrapper {

    private static ApplicationContext applicationContext;

    public static void setContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
