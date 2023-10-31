package cz.upce.gremis.gremisng.organization.ui.boards;

import cz.upce.gremis.gremisng.domain.Done;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.organization.service.BoardAggregateService;
import cz.upce.gremis.gremisng.ui.AsyncDataCallback;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
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
class BoardViewData {

    private final BoardAggregateService boardAggregateService;

    BoardViewData(BoardAggregateService boardAggregateService) {
        this.boardAggregateService = boardAggregateService;
    }

    @Async("readOnlyOpsTaskExecutor")
    void getBoard(Long boardId, AsyncDataCallback<Board> callback) {
        try {
            log.info("Loading board - id: {}", boardId);
            var board = boardAggregateService.getBoard(boardId);
            callback.operationFinished(board);
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }

    @Async("readOnlyOpsTaskExecutor")
    void getTerms(Long boardId, AsyncDataCallback<List<TermDto>> callback) {
        try {
            log.info("Loading terms - boardId: {}", boardId);
            callback.operationFinished(boardAggregateService.getTerms(boardId));
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }

    @Async("readOnlyOpsTaskExecutor")
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
    public void updateCommittees(Long boardId, Collection<Committee> committees, AsyncDataCallback<Done> callback) {
        withCallback(callback, () -> {
            boardAggregateService.updateCommittees(boardId, committees);
            return Done.INSTANCE;
        });
    }

    public void save(Board board, AsyncDataCallback<Board> callback) {
        withCallback(callback, () -> boardAggregateService.save(board));
    }

    @Async
    public CompletableFuture<BoardTerm> saveTerm(BoardTerm boardTerm) {
        return CompletableFuture.completedFuture(boardAggregateService.save(boardTerm));
    }

    @Async("readOnlyOpsTaskExecutor")
    public void getCurrentTerm(Long boardId, AsyncDataCallback<Optional<TermDto>> callback) {
        log.info("Getting current term");
        var term = boardAggregateService.getTerms(boardId).stream().findFirst();
        callback.operationFinished(term);
    }

    @Async("readOnlyOpsTaskExecutor")
    public void getTerm(Long termId, AsyncDataCallback<BoardTerm> callback) {
        withCallback(callback, () -> boardAggregateService.getTerm(termId));
    }

    @Async
    public void save(Committee committee, AsyncDataCallback<Committee> callback) {
        withCallback(callback, () -> boardAggregateService.saveCommittee(committee));
    }

    @Async
    void save(BoardSeat seat, AsyncDataCallback<BoardSeat> callback) {
        withCallback(callback, () -> boardAggregateService.save(seat));
    }

    @Async
    public void getCommittees(Long boardId, AsyncDataCallback<List<Committee>> callback) {
        withCallback(callback, () -> boardAggregateService.getCommittees(boardId));
    }

    @Async
    public void removeCommittee(Committee committee, AsyncDataCallback<Done> callback) {
        withCallback(callback, () -> {
            boardAggregateService.remove(committee);
            return Done.INSTANCE;
        });
    }

    @Async
    public void removeSeat(BoardSeat boardSeat, AsyncDataCallback<Done> callback) {
        withCallback(callback, () -> {
            boardAggregateService.removeSeat(boardSeat);
            return Done.INSTANCE;
        });
    }

    private <T> void withCallback(AsyncDataCallback<T> callback, Supplier<T> action) {
        try {
            callback.operationFinished(action.get());
        } catch (RuntimeException ex) {
            callback.operationFailed(ex);
        }
    }
}
