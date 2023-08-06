package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.voting.domain.VoteDto;
import cz.upce.gremis.gremisng.voting.domain.VoteRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class VotesDataProvider extends AbstractBackEndDataProvider<VoteDto, VoteFilter> {

    private final VoteRepository voteRepository;

    VotesDataProvider(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    protected Stream<VoteDto> fetchFromBackEnd(Query<VoteDto, VoteFilter> query) {
        return getVotes(query).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<VoteDto, VoteFilter> query) {
        return (int)voteRepository.count();
    }

    private Page<VoteDto> getVotes(Query<VoteDto, VoteFilter> query) {
        var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
        return voteRepository.page(pageRequest);
    }
}
