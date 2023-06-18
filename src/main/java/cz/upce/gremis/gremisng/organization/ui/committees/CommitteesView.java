package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.organization.domain.BoardNameDto;
import cz.upce.gremis.gremisng.organization.domain.CommitteeListItemView;
import cz.upce.gremis.gremisng.organization.ui.dataproviders.BoardNamesDataProvider;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.ui.components.RendererTemplates;

import static cz.upce.gremis.gremisng.ui.components.SpanHelpers.setupStateSpan;

@Route(value = "/committees", layout = MainLayout.class)
public class CommitteesView extends VerticalLayout implements HasDynamicTitle {

    private final CommitteesDataProvider committeesDataProvider;

    private final CommitteeFilter filter = new CommitteeFilter();

    CommitteesView(CommitteesDataProvider committeesDataProvider, BoardNamesDataProvider boardNamesDataProvider) {
        this.committeesDataProvider = committeesDataProvider;

        configureGrid(boardNamesDataProvider);
    }

    private void configureGrid(BoardNamesDataProvider boardNamesDataProvider) {

        // Tabs
        Tab activeTab = new Tab("Aktivní");
        Tab allTab = new Tab("Vše");

        Tabs tabs = new Tabs(activeTab, allTab);
        tabs.setWidthFull();
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED, TabsVariant.LUMO_SMALL);

        ConfigurableFilterDataProvider<CommitteeListItemView, Void, CommitteeFilter> filterDataProvider = committeesDataProvider
                .withConfigurableFilter();
        filter.setActiveOnly(true);
        filterDataProvider.setFilter(filter);

        tabs.addSelectedChangeListener(e -> {
            filter.setActiveOnly(activeTab == e.getSelectedTab());
            filterDataProvider.setFilter(filter);
        });

        var aboveGrid = new HorizontalLayout();

        aboveGrid.setWidthFull();

        var span = new Span("Filtry:");
        span.addClassNames(LumoUtility.Padding.Top.LARGE);

        aboveGrid.setVerticalComponentAlignment(Alignment.CENTER, span);
        aboveGrid.add(span);

        var searchField = configureSearchField();
        searchField.addValueChangeListener(e -> {
            filter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(filter);
        });

        aboveGrid.add(searchField);

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

        var grid = new Grid<CommitteeListItemView>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.addColumn(createCommitteeRenderer()).setSortable(true).setSortProperty("name").setHeader("Název");
        grid.addColumn(CommitteeListItemView::boardName).setHeader("Grémium");
        grid.addColumn(CommitteeListItemView::durationInMonths).setHeader("Doba v měsících");
        grid.addColumn(createStatusComponentRenderer()).setHeader("Stav").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

        // actions
        grid.addItemClickListener(e ->
                grid.getUI().ifPresent(ui -> ui.navigate(CommitteeDetailView.class, e.getItem().id())));

        grid.setItems(filterDataProvider);

        add(tabs, aboveGrid, grid);
    }

    private TextField configureSearchField() {
        var searchField = new TextField("Název nebo zkratka");
        searchField.setWidth("25%");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setClearButtonVisible(true);
        return searchField;
    }

    @Override
    public String getPageTitle() {
        return "Komise";
    }

    private static Renderer<CommitteeListItemView> createCommitteeRenderer() {
        return RendererTemplates.createNameAndAbbreviationRenderer(CommitteeListItemView::name, CommitteeListItemView::abbreviation);
    }

    private static ComponentRenderer<Span, CommitteeListItemView> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }

    private static final SerializableBiConsumer<Span, CommitteeListItemView> statusComponentUpdater = (span, listItemView) -> {
        boolean isActive = listItemView.active();
        setupStateSpan(span, isActive);
    };
}
