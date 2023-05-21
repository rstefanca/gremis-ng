package cz.upce.gremis.gremisng.core.events;

import cz.upce.gremis.gremisng.core.AbstractEntity;
import org.jmolecules.event.types.DomainEvent;

public record EntityCreatedEvent(AbstractEntity entity) implements DomainEvent {
}
