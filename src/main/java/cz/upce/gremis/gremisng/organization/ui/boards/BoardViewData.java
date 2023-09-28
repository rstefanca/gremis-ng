package cz.upce.gremis.gremisng.organization.ui.boards;

import cz.upce.gremis.gremisng.domain.Done;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.organization.service.BoardAggregateService;
import cz.upce.gremis.gremisng.ui.AsyncDataCallback;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static cz.upce.gremis.gremisng.domain.Done.done;

/**
 * A facade for asynchronously accessing Board aggregate data.
 */
//TODO decide completable futures or callback
@Log4j2
@Component
public class BoardViewData {

    private final BoardAggregateService boardAggregateService;

    BoardViewData(BoardAggregateService boardAggregateService) {
        this.boardAggregateService = boardAggregateService;
    }

    @Async
    void getBoard(Long boardId, AsyncDataCallback<Board> callback) {
        //callback.operationFailed(new RuntimeException("oops"));
        try {
            log.info("Loading board - id: {}", boardId);
            var board = boardAggregateService.getBoard(boardId);
            callback.operationFinished(board);
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }

    @Async
    void getTerms(Long boardId, AsyncDataCallback<List<TermDto>> callback) {
        try {
            log.info("Loading terms - boardId: {}", boardId);
            callback.operationFinished(boardAggregateService.getTerms(boardId));
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }

    @Async
    void getSeats(Long boardId, AsyncDataCallback<List<BoardSeat>> callback) {
        try {
            boardAggregateService.getBoardSeats(boardId, seats -> callback.operationFinished(seats.toList()));
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }

    @Async
    public void updateSeats(Long boardId, Collection<BoardSeat> boardSeats, AsyncDataCallback<Done> callback) {
        try {
            boardAggregateService.updateSeats(boardId, boardSeats);
            callback.operationFinished(done());
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }

    @Async
    public CompletableFuture<Void> save(Board board) {
        boardAggregateService.save(board);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<BoardTerm> saveTerm(BoardTerm boardTerm) {
        return CompletableFuture.completedFuture(boardAggregateService.save(boardTerm));
    }


    @Async
    public void getCurrentTerm(Long boardId, AsyncDataCallback<Optional<TermDto>> callback) {
        log.info("Getting current term");
        var term = boardAggregateService.getTerms(boardId).stream().findFirst();
        callback.operationFinished(term);
    }

    @Async
    public void getTerm(Long termId, AsyncDataCallback<BoardTerm> callback) {
        withCallback(callback, () -> boardAggregateService.getTerm(termId));
    }

    private <T> void withCallback(AsyncDataCallback<T> callback, Supplier<T> action) {
        try {
            callback.operationFinished(action.get());
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }
    
    @Async
    public void save(Committee committee, AsyncDataCallback<Committee> callback) {
        withCallback(callback, () -> boardAggregateService.saveCommittee(committee));
    }
}
