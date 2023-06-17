package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.upce.gremis.gremisng.assignments.domain.AbstractAssignment;
import cz.upce.gremis.gremisng.assignments.domain.CommitteeSeatAssignment;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;


@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class CommitteeAssignmentsGrid extends Div {

    private final Grid<CommitteeSeatAssignment> grid = new Grid<>();

    private final AssignmentsDataProvider assignmentsDataProvider;

    private GridDataView<CommitteeSeatAssignment> gridDataView;

    private Long termId;

    private final AssignmentFilter filter = new AssignmentFilter();

    public CommitteeAssignmentsGrid(AssignmentsDataProvider assignmentsDataProvider) {
        this.assignmentsDataProvider = assignmentsDataProvider;
        setWidthFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPageSize(15);
        grid.setMaxHeight("500px");
        grid.setWidthFull();

        var firstColumn = grid.addColumn(it -> it.getPerson().getFullName())
                .setAutoWidth(true)
                .setHeader("Osoba")
                .setComparator(it -> it.getPerson().getLastName());


        grid.addColumn(AbstractAssignment::getName).setHeader("Funkce")
                .setSortable(true);
        grid.addColumn(AbstractAssignment::getStart).setHeader("Ve funkci od")
                .setSortable(true);
        grid.addColumn(AbstractAssignment::getEnd).setHeader("Ve funkci do");

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, assignment) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(clickEvent -> removeAssignment(assignment));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setWidth("6em").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

        add(grid);
    }

    void init() {
        if (gridDataView == null) {
            var provider = assignmentsDataProvider.withConfigurableFilter();
            provider.setFilter(filter);
            gridDataView = grid.setItems(provider);
        }
    }

    void setTermId(Long termId) {
        filter.setTermId(termId);
        this.termId = termId;
    }

    void refresh() {
        if (gridDataView != null) {
            gridDataView.refreshAll();
        }
    }

    private void removeAssignment(AbstractAssignment it) {

    }
}
