package cz.upce.gremis.gremisng.utils;

import cz.upce.gremis.gremisng.assignments.service.BoardSeatAssignmentService;
import cz.upce.gremis.gremisng.common.GremisRole;
import cz.upce.gremis.gremisng.common.PerRollamApproveType;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.organization.service.BoardAggregateService;
import cz.upce.gremis.gremisng.persons.domain.PersonRepository;
import cz.upce.gremis.gremisng.time.DateInterval;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Order(1)
class MyApplicationListener
        implements ApplicationListener<ApplicationReadyEvent> {

    private final BoardAggregateService boardAggregateService;
    private final BoardSeatAssignmentService boardSeatAssignmentService;

    private final PersonRepository personRepository;

    MyApplicationListener(BoardAggregateService boardAggregateService,
                          BoardSeatAssignmentService boardSeatAssignmentService, PersonRepository personRepository) {
        this.boardAggregateService = boardAggregateService;
        this.boardSeatAssignmentService = boardSeatAssignmentService;
        this.personRepository = personRepository;
    }


    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {

        var board = new Board();
        board.setName("Test board");
        board.setAbbreviation("TXB");
        board.setTermType(TermType.BOARD);
        board.setPerRollamApproveType(PerRollamApproveType.NONE);
        board.setMinMemberCount(3);
        board.setCommitteesEnabled(true);

        var term1 = new BoardTerm()
                .setDescription("Funkcni obdobi 2023")
                .setStart(LocalDate.of(2023, 1, 13))
                .setEnd(LocalDate.of(2024, 1, 12));
        board.add(term1);

        var term2 = new BoardTerm()
                .setDescription("Funkcni obdobi 2024")
                .setStart(LocalDate.of(2024, 1, 13))
                .setEnd(LocalDate.of(2025, 1, 12));
        board.add(term2);

        var seat = new BoardSeat()
                .setName("Senátor")
                .setDuration(36)
                .setSettings(new SeatSettings()
                        .setFilling(Filling.MANDATORY)
                        .setAssignmentType(AssignmentType.MULTIPLE_PERSONS)
                        .setGrantedRole(GremisRole.MEMBER)
                        .setMinMemberCount(4));

        board.add(seat);

        var committee = new Committee()
                .setName("Ekonomicka komise")
                .setAbbreviation("AS_EKO")
                .setMinimumSeats(3)
                .setTermDurationInMonths(36)
                .setMandatory(true);

        committee.setBoard(board);


        boardAggregateService.save(board);
        boardAggregateService.saveCommittee(committee);


        var dateInterval = DateInterval.of(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 12));
        personRepository.findAll().stream().skip(1).limit(40).forEach(person -> {
            boardSeatAssignmentService.create(seat, term1, person, dateInterval, "Test");
        });


    }

}