package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.core.ContextWrapper;
import jakarta.persistence.PrePersist;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CommitteeSeatListener {

    private CommitteeSeatRepository repository;

    @PrePersist
    public void beforePersist(CommitteeSeat seat) {
        //update order
        if (repository == null) {
            initRepository();
        }
        log.debug("Updating committee order");
        var index = repository.countSeats(seat.getCommittee().getId());
        seat.setOrder(index + 1);
    }

    private synchronized void initRepository() {
        if (repository == null) {
            repository = ContextWrapper.getApplicationContext().getBean(CommitteeSeatRepository.class);
        }
    }
}
