package cz.upce.gremis.gremisng.security;

import cz.upce.gremis.gremisng.assignments.domain.BoardSeatAssignment;
import cz.upce.gremis.gremisng.core.events.EntityCreatedEvent;
import cz.upce.gremis.gremisng.persons.domain.RoleOrigin;
import cz.upce.gremis.gremisng.persons.domain.SystemRole;
import cz.upce.gremis.gremisng.persons.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DomainEventListeners {

    private final PersonService personService;

    public DomainEventListeners(PersonService personService) {
        this.personService = personService;
    }

    @EventListener(EntityCreatedEvent.class)
    public void on(EntityCreatedEvent event) {
        var entity = event.entity();
        if (entity instanceof BoardSeatAssignment boardSeatAssignment) {
            log.debug("On: {}", ClassUtils.getSimpleName(event));
            var grantedRole = boardSeatAssignment.getSeat().getSettings().getGrantedRole();
            if (grantedRole != null) {
                var person = boardSeatAssignment.getPerson();
                var boardId = boardSeatAssignment.getTerm().getBoard().getId();

                var systemRole = new SystemRole()
                        .setRoleOrigin(RoleOrigin.BOARD)
                        .setPermissionLevel(grantedRole)
                        .setBoardId(boardId)
                        .setPerson(person)
                        .setStart(boardSeatAssignment.getStart())
                        .setEnd(boardSeatAssignment.getEnd());

                personService.saveSystemRole(systemRole);
            }
        }
    }
}
