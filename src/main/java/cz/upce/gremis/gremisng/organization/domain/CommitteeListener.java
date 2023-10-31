package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.core.ContextWrapper;
import jakarta.persistence.PrePersist;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class CommitteeListener  {

    private CommitteeRepository committeeRepository;

    @PrePersist
    public void beforePersist(Committee committee) {
        //update order
        if (committeeRepository == null) {
            initRepository();
        }
        log.debug("Updating committee order");
        var index = committeeRepository.countCommittees(committee.getBoard().getId());
        committee.setOrder(index + 1);
    }

    private synchronized void initRepository() {
        if (committeeRepository == null) {
            committeeRepository = ContextWrapper.getApplicationContext().getBean(CommitteeRepository.class);
        }
    }

}
