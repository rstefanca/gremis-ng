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
import cz.upce.gremis.gremisng.organization.domain.WorkingGroup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkingGroupsGrid extends Div {

    private final Grid<WorkingGroup> grid = new Grid<>();

    private final WorkingGroupFilter filter = new WorkingGroupFilter();

    private final WorkingGroupsDataProvider dataProvider;

    private GridDataView<WorkingGroup> gridDataView;

    public WorkingGroupsGrid(WorkingGroupsDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        setWidthFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setPageSize(15);
        grid.setMaxHeight("500px");
        grid.setWidthFull();

        grid.addColumn(WorkingGroup::getName).setHeader("Název");
        grid.addColumn(WorkingGroup::getStart).setHeader("Od")
                .setSortable(true)
                .setSortProperty("start");
        grid.addColumn(WorkingGroup::getEnd).setHeader("Do");

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, workingGroup) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(clickEvent -> removeWorkingGroup(workingGroup));
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setWidth("6em").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

        add(grid);
    }

    void init() {
        if (gridDataView == null) {
            var provider = dataProvider.withConfigurableFilter();
            provider.setFilter(filter);
            gridDataView = grid.setItems(provider);
        }
    }

    void refresh() {
        if (gridDataView != null) {
            gridDataView.refreshAll();
        }
    }

    private void removeWorkingGroup(WorkingGroup workingGroup) {

    }

    void setTermId(Long termId) {
        filter.setTermId(termId);
    }

    Long getTermId() {
        return filter.getTermId();
    }
}
