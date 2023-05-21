package cz.upce.gremis.gremisng.ui;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ServiceListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        VaadinService vaadinService = event.getSource();
        vaadinService.addSessionInitListener(initEvent ->
                log.info("A new Session has been initialized"));
        vaadinService.addUIInitListener(initEvent ->
                log.info("A new UI has been initialized"));
    }
}
