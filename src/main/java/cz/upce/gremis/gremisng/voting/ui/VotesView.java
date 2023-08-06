package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.organization.domain.BoardNameDto;
import cz.upce.gremis.gremisng.organization.ui.committees.CommitteeDetailView;
import cz.upce.gremis.gremisng.organization.ui.dataproviders.BoardNamesDataProvider;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.voting.domain.VoteDto;

@Route(value = "/votes", layout = MainLayout.class)
public class VotesView extends VerticalLayout {

    private final VoteFilter filter = new VoteFilter();

    public VotesView(VotesDataProvider votesDataProvider,
                     BoardNamesDataProvider boardNamesDataProvider) {
        add(new H1("Hlasování"));

        setWidthFull();
        setHeightFull();

        ConfigurableFilterDataProvider<VoteDto, Void, VoteFilter> filterDataProvider = votesDataProvider
                .withConfigurableFilter();
        filterDataProvider.setFilter(filter);

        var aboveGrid = new HorizontalLayout();

        aboveGrid.setWidthFull();

        var span = new Span("Filtry:");
        span.addClassNames(LumoUtility.Padding.Top.LARGE);

        aboveGrid.setVerticalComponentAlignment(Alignment.CENTER, span);
        aboveGrid.add(span);

        var searchField = configureSearchField();
        searchField.addValueChangeListener(e -> {
            filter.setName(e.getValue());
            filterDataProvider.setFilter(filter);
        });

        aboveGrid.add(searchField);

        var grid = new Grid<VoteDto>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.addColumn(VoteDto::name).setHeader("Název");
        grid.addColumn(VoteDto::boardName).setHeader("Grémuim");
        grid.addColumn(VoteDto::voteState).setHeader("Stav");
        grid.addColumn(VoteDto::startTime).setHeader("Začátek");
        grid.addColumn(VoteDto::endTime).setHeader("Konec");

        // actions
        grid.addItemClickListener(e ->
                grid.getUI().ifPresent(ui -> ui.navigate(VoteView.class, e.getItem().id())));

        grid.setItems(filterDataProvider);

        var boardSelect = new MultiSelectComboBox<BoardNameDto>("Grémium");
        boardSelect.setAllowCustomValue(false);
        boardSelect.setClearButtonVisible(true);
        boardSelect.setItemLabelGenerator(BoardNameDto::name);
        boardSelect.setItems(boardNamesDataProvider);
        boardSelect.addSelectionListener(e -> {
            filter.resetBoardIds();
            e.getValue().forEach(it -> filter.addBoardId(it.id()));
            filterDataProvider.setFilter(filter);
        });
        boardSelect.setWidth("450px");
        aboveGrid.add(boardSelect);

        add(aboveGrid, grid);
    }

    private TextField configureSearchField() {
        var searchField = new TextField("Název");
        searchField.setWidth("25%");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setClearButtonVisible(true);
        return searchField;
    }
}
