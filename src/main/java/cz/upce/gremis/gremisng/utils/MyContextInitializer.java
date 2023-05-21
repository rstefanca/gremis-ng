package cz.upce.gremis.gremisng.utils;

import cz.upce.gremis.gremisng.core.ContextWrapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class MyContextInitializer implements ApplicationListener<ApplicationReadyEvent> {



    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ContextWrapper.setContext(event.getApplicationContext());
    }
}
