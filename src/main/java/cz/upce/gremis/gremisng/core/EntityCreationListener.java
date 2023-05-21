package cz.upce.gremis.gremisng.core;

import cz.upce.gremis.gremisng.core.events.EntityCreatedEvent;
import jakarta.persistence.PostPersist;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class EntityCreationListener {

    private ApplicationEventPublisher eventPublisher;

    @PostPersist
    void fireEntityCreated(AbstractEntity entity) {
        log.debug("Entity created: {}", ClassUtils.getSimpleName(entity));
        eventPublisher.publishEvent(new EntityCreatedEvent(entity));
    }

    @Autowired
    void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
