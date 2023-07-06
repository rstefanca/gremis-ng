package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.organization.domain.WorkingGroup;
import cz.upce.gremis.gremisng.organization.domain.WorkingGroupRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
class WorkingGroupsDataProvider extends AbstractBackEndDataProvider<WorkingGroup, WorkingGroupFilter> {

    private final WorkingGroupRepository repository;

    public WorkingGroupsDataProvider(WorkingGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Stream<WorkingGroup> fetchFromBackEnd(Query<WorkingGroup, WorkingGroupFilter> query) {
        var termId = query.getFilter().get().getTermId();
        var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
        return repository.findByTermId(termId, pageRequest).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<WorkingGroup, WorkingGroupFilter> query) {
        var termId = query.getFilter().get().getTermId();
        return (int) repository.countByTermId(termId);
    }
}
