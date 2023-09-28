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

    BoardAggregateService(BoardRepository boardRepository,
                          BoardTermRepository boardTermRepository,
                          CommitteeRepository committeeRepository) {
        this.repository = boardRepository;
        this.boardTermRepository = boardTermRepository;
        this.committeeRepository = committeeRepository;
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

    public void getCommittees(Long boardId, Consumer<Stream<Committee>> onCommittees) {
        onCommittees.accept(repository.getCommittees(boardId));
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

    public BoardTerm getTerm(Long termId) {
        return boardTermRepository.findById(termId).orElseThrow(() -> new DataNotFoundException("Board term not found"));
    }

    @Transactional
    public BoardTerm save(BoardTerm boardTerm) {
        return boardTermRepository.save(boardTerm);
    }
}
