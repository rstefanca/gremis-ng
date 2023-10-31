package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.core.ContextWrapper;
import jakarta.persistence.PrePersist;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BoardSeatListener {

    private BoardSeatRepository repository;

    @PrePersist
    public void beforePersist(BoardSeat boardSeat) {
        if (repository == null) {
            initRepository();
        }
        log.debug("Updating board seat order");
        var index = repository.countBoardSeats(boardSeat.getBoard().getId());
        boardSeat.setOrder(index + 1);
    }

    private synchronized void initRepository() {
        if (repository == null) {
            repository = ContextWrapper.getApplicationContext().getBean(BoardSeatRepository.class);
        }
    }

}
