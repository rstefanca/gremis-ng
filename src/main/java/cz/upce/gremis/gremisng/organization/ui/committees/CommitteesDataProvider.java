package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.organization.domain.Committee;
import cz.upce.gremis.gremisng.organization.domain.CommitteeListItemView;
import cz.upce.gremis.gremisng.organization.domain.CommitteeRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommitteesDataProvider extends AbstractBackEndDataProvider<CommitteeListItemView, CommitteeFilter> {

    private final CommitteeRepository committeeRepository;

    CommitteesDataProvider(CommitteeRepository committeeRepository) {
        this.committeeRepository = committeeRepository;
    }

    @Override
    protected Stream<CommitteeListItemView> fetchFromBackEnd(Query<CommitteeListItemView, CommitteeFilter> query) {
        Page<Committee> result = getCommittees(query);

        return result.stream().map(committee -> new CommitteeListItemView(
                committee.getId(),
                committee.getName(),
                committee.getAbbreviation(),
                committee.isMandatory(),
                committee.isWorkingGroupsEnabled(),
                committee.getTermDurationInMonths(),
                committee.getBoard().getId(),
                committee.getBoard().getName(),
                committee.isActive()
        ));
    }

    private Page<Committee> getCommittees(Query<CommitteeListItemView, CommitteeFilter> query) {
        var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);

        return query.getFilter()
                .map(f -> findByFilter(f, pageRequest))
                .orElseGet(() -> getAll(pageRequest));
    }

    private Page<Committee> getAll(PageRequest pageRequest) {
        return committeeRepository.findAll(pageRequest);
    }

    private Page<Committee> findByFilter(CommitteeFilter filter, Pageable pageRequest) {
        var searchTerm = filter.getSearchTerm() != null ? "%" + filter.getSearchTerm() + "%" : null;
        var activeFilter = filter.isActiveOnly() ? Boolean.TRUE : null;
        var boardIds = filter.hasBoardIds() ? new ArrayList<>(filter.getBoardIds()) : null;
        return committeeRepository.complexQuery(searchTerm, activeFilter, boardIds, pageRequest);
    }

    @Override
    protected int sizeInBackEnd(Query<CommitteeListItemView, CommitteeFilter> query) {
        return (int) getCommittees(query).getTotalElements();
    }
}
