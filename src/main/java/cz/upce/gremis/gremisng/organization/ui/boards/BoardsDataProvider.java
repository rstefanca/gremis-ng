package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import cz.upce.gremis.gremisng.organization.domain.BoardRepository;
import cz.upce.gremis.gremisng.organization.domain.BoardListItemView;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@SuppressWarnings("DataFlowIssue")
@Component
@Transactional(readOnly = true)
class BoardsDataProvider extends AbstractBackEndDataProvider<BoardListItemView, BoardsFilter> {

    private final BoardRepository boardRepository;

    BoardsDataProvider(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    protected Stream<BoardListItemView> fetchFromBackEnd(Query<BoardListItemView, BoardsFilter> query) {
        var pageable = Pageable.ofSize(query.getPageSize()).withPage(query.getPage());
        var page = boardRepository.findAll(pageable);
        return page.stream().map(b ->
                new BoardListItemView(b.getId(),
                        b.getName(),
                        b.getAbbreviation(),
                        b.getTermType(),
                        b.isActive())
        );
    }

    @Override
    protected int sizeInBackEnd(Query<BoardListItemView, BoardsFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}
