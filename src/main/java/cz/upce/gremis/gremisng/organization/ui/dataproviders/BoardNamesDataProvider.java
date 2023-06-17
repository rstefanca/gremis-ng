package cz.upce.gremis.gremisng.organization.ui.dataproviders;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.organization.domain.BoardNameDto;
import cz.upce.gremis.gremisng.organization.domain.BoardRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class BoardNamesDataProvider extends AbstractBackEndDataProvider<BoardNameDto, String> {

    private final BoardRepository boardRepository;

    BoardNamesDataProvider(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    protected Stream<BoardNameDto> fetchFromBackEnd(Query<BoardNameDto, String> query) {
        Page<BoardNameDto> page = selectNames(query);
        return page.stream();
    }

    private Page<BoardNameDto> selectNames(Query<BoardNameDto, String> query) {
        var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
        var searchTerm = query.getFilter().map(f -> "%" + f + "%").orElse(null);
        return boardRepository.selectNames(searchTerm, pageRequest);
    }

    @Override
    protected int sizeInBackEnd(Query<BoardNameDto, String> query) {
        return (int) selectNames(query).getTotalElements();
    }
}
