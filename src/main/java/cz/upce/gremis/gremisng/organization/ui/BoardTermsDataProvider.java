package cz.upce.gremis.gremisng.organization.ui;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.organization.domain.BoardTermRepository;
import cz.upce.gremis.gremisng.organization.domain.TermDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Stream;

@Component
@Transactional(readOnly = true)
public class BoardTermsDataProvider implements CallbackDataProvider.FetchCallback<TermDto, String> {

    private final BoardTermRepository boardTermRepository;

    private final TermDto single = new TermDto(1L, "Test", LocalDate.now(), LocalDate.now().plusYears(1));

    BoardTermsDataProvider(BoardTermRepository boardTermRepository) {
        this.boardTermRepository = boardTermRepository;
    }

    @Override
    public Stream<TermDto> fetch(Query<TermDto, String> query) {
        //System.out.println(query.getLimit());

        var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
        return Stream.of(single);
    }

    public TermDto getOne(Long id) {
        System.out.println("getOne");
        return single;
    }
}
