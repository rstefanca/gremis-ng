package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.assignments.domain.CommitteeSeatAssignment;
import cz.upce.gremis.gremisng.assignments.service.CommitteeSeatAssignmentService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
class AssignmentsDataProvider extends AbstractBackEndDataProvider<CommitteeSeatAssignment, AssignmentFilter> {

    private final CommitteeSeatAssignmentService service;

    AssignmentsDataProvider(CommitteeSeatAssignmentService service) {
        this.service = service;
    }

    @Override
    protected Stream<CommitteeSeatAssignment> fetchFromBackEnd(Query<CommitteeSeatAssignment, AssignmentFilter> query) {
        var termId = query.getFilter().get().getTermId();
        var pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
        return service.findByTermId(termId, pageRequest).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<CommitteeSeatAssignment, AssignmentFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}
