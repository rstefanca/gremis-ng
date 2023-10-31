package cz.upce.gremis.gremisng.assignments.service;

import cz.upce.gremis.gremisng.assignments.domain.BoardSeatAssignment;
import cz.upce.gremis.gremisng.assignments.domain.BoardSeatAssignmentRepository;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.time.DateInterval;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Consumer;

@Log4j2
@Service
public class BoardSeatAssignmentService {

    private final BoardSeatAssignmentRepository repository;

    public BoardSeatAssignmentService(BoardSeatAssignmentRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public BoardSeatAssignment create(BoardSeat seat, BoardTerm term, Person person, LocalDate start, LocalDate end, String note) {
        //TODO some logic
        var assignment = (BoardSeatAssignment) new BoardSeatAssignment()
                .setTerm(term)
                .setSeat(seat)
                .setPerson(person)
                .setStart(start)
                .setEnd(end)
                .setName("why")
                .setNote(note);

        return repository.save(assignment);
    }

    @Transactional
    public BoardSeatAssignment create(BoardSeat seat, BoardTerm term, Person person, DateInterval dateInterval) {
        return create(seat, term, person, dateInterval, null);
    }

    @Transactional
    public BoardSeatAssignment create(BoardSeat seat, BoardTerm term, Person person, DateInterval dateInterval, String note) {
        return create(seat, term, person, dateInterval.getStart(), dateInterval.getEnd(), note);
    }

    @Transactional(readOnly = true)
    public void getAssignmentsByTermId(Long termId, Consumer<Collection<BoardSeatAssignment>> onSeats) {
        onSeats.accept(repository.findByTermId(termId).toList());
    }

    @Transactional(readOnly = true)
    public Page<BoardSeatAssignment> findByTermId(Long termId, Pageable pageable) {
        return repository.findByTerm_Id(termId, pageable);
    }

    @Transactional
    public void save(BoardSeatAssignment boardSeatAssignment) {
        repository.save(boardSeatAssignment);
    }

    @Transactional
    public void remove(BoardSeatAssignment assignment) {
        log.info("Removing board seat assignment - id={}", assignment.getId());
        repository.deleteById(assignment.getId());
    }
}
