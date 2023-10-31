package cz.upce.gremis.gremisng.organization.service;

import cz.upce.gremis.gremisng.core.DataNotFoundException;
import cz.upce.gremis.gremisng.organization.domain.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@Transactional(readOnly = true)
public class BoardAggregateService {

    private final BoardRepository repository;

    private final BoardTermRepository boardTermRepository;

    private final CommitteeRepository committeeRepository;

    private final BoardSeatRepository boardSeatRepository;

    BoardAggregateService(BoardRepository boardRepository,
                          BoardTermRepository boardTermRepository,
                          CommitteeRepository committeeRepository,
                          BoardSeatRepository boardSeatRepository) {
        this.repository = boardRepository;
        this.boardTermRepository = boardTermRepository;
        this.committeeRepository = committeeRepository;
        this.boardSeatRepository = boardSeatRepository;
    }

    @Transactional
    public Board save(Board board) {
        log.info("Saving board");
        return repository.save(board);
    }

    @Transactional
    public void remove(BoardTerm boardTerm) {
       boardTermRepository.delete(boardTerm);
       log.info("Board term removed id={}", boardTerm.getId());
    }

    @Transactional
    public void remove(Committee committee) {
        committeeRepository.delete(committee);
        log.info("Committee removed id={}", committee.getId());
    }

    public void getBoard(Long id, Consumer<Board> consumer) {
        var board = repository.getReferenceById(id);
        consumer.accept(board);
    }

    public Board getBoard(Long id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Board not found"));
    }

    public void getBoardSeats(Long boardId, Consumer<Stream<BoardSeat>> onSeats) {
        var seats = repository.getSeats(boardId).sorted(Comparator.comparing(BoardSeat::getOrder));
        onSeats.accept(seats);
    }

    @Transactional
    public Committee saveCommittee(Committee committee) {
        var saved = committeeRepository.save(committee);
        log.info("Saved committee");
        return saved;
    }

    @Deprecated
    public void consumeCommittees(Long boardId, Consumer<Stream<Committee>> onCommittees) {
        onCommittees.accept(repository.getCommittees(boardId));
    }

    public List<Committee> getCommittees(Long boardId) {
        return repository.getCommittees(boardId).toList();
    }

    //Cacheable?
    public List<TermDto> getTerms(Long boardId) {
        return boardTermRepository.getTerms(boardId);
    }

    @Transactional
    public void updateSeats(Long boardId, Collection<BoardSeat> boardSeats) {
        log.info("Updating seats - boardId = {}", boardId);
        var board = repository.getReferenceById(boardId);
        var orderById = boardSeats.stream().collect(Collectors.toMap(BoardSeat::getId, BoardSeat::getOrder));

        board.getSeats().forEach(s -> s.setOrder(orderById.get(s.getId())));

        save(board);
        log.info("Updated seats - boardId = {}", boardId);
    }

    @Transactional
    public void updateCommittees(Long boardId, Collection<Committee> committees) {
        log.info("Updating committees - boardId = {}", boardId);
        var board = repository.getReferenceById(boardId);
        var orderById = committees.stream().collect(Collectors.toMap(Committee::getId, Committee::getOrder));
        board.getCommittees().forEach(c -> c.setOrder(orderById.get(c.getId())));

        save(board);
        log.info("Updated seats - boardId = {}", boardId);
    }

    public BoardTerm getTerm(Long termId) {
        return boardTermRepository.findById(termId).orElseThrow(() -> new DataNotFoundException("Board term not found"));
    }

    @Transactional
    public BoardTerm save(BoardTerm boardTerm) {
        return boardTermRepository.save(boardTerm);
    }

    @Transactional
    public BoardSeat save(BoardSeat boardSeat) {
        log.info("Saving board seat - {}", boardSeat.getName());
        if (boardSeat.isNew()) {
            var count = boardSeatRepository.countBoardSeats(boardSeat.getBoard().getId());
            boardSeat.setOrder(count + 1);
        }
        return boardSeatRepository.save(boardSeat);
    }

    @Transactional
    public void removeSeat(BoardSeat boardSeat) {
        log.info("Removing board seat - {}", boardSeat.getId());
        boardSeatRepository.delete(boardSeat);
    }
}
