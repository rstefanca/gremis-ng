package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.organization.domain.BoardListItemView;
import cz.upce.gremis.gremisng.ui.MainLayout;

import static cz.upce.gremis.gremisng.organization.ui.boards.RendererTemplates.NAME_AND_ABBREVIATION_TEMPLATE;
import static cz.upce.gremis.gremisng.ui.components.SpanHelpers.setupStateSpan;

@Route(value = "/boards", layout = MainLayout.class)
public class BoardsView extends VerticalLayout {



    private final BoardsFilter boardsFilter = new BoardsFilter();


    public BoardsView(BoardsDataProvider boardsDataProvider) {
        ConfigurableFilterDataProvider<BoardListItemView, Void, BoardsFilter> filterDataProvider = boardsDataProvider
                .withConfigurableFilter();

        //search
        TextField searchField = prepareSearchField();
        searchField.addValueChangeListener(e -> {
            boardsFilter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(boardsFilter);
        });
        searchField.setClearButtonVisible(true);

        var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        var menuDiv = new Div(menu);
        menuDiv.setWidthFull();
        var top = new HorizontalLayout(searchField, menuDiv);
        top.setWidthFull();
        var addPersonMenuItem = menu.addItem(new Icon(VaadinIcon.PLUS));
        addPersonMenuItem.add("Nove gremium");
        addPersonMenuItem.addClickListener(e -> {
            getUI().get().navigate(NewBoardView.class);
        });

        var grid = new Grid<BoardListItemView>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


        grid.addColumn(createBoardRenderer()).setHeader("Nazev gremia");//.setFooter(String.format("%s gremii celkem", boardsDataProvider.sizeInBackEnd(new Query<>())));

        grid.addColumn((ValueProvider<BoardListItemView, String>) board -> getTranslation("enum.termType." + board.termType())).setHeader("Typ funckniho obdobi");

        grid.addColumn(createStatusComponentRenderer()).setHeader("Status").setResizable(false).setFlexGrow(0);

        grid.setItems(filterDataProvider);

        GridContextMenu<BoardListItemView> contextMenu = grid.addContextMenu();

        contextMenu.addItem("Edit", event -> {
            event.getItem().ifPresent(board ->
                    getUI().ifPresent(ui -> ui.navigate(BoardDetailView.class, board.id())));
        });
        contextMenu.addItem("Delete", event -> {

        });


        // Tabs
        Tab activeTab = new Tab("Aktivní");
        Tab allTab = new Tab("Vše");

        Tabs tabs = new Tabs(activeTab, allTab);
        tabs.setWidthFull();
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED, TabsVariant.LUMO_SMALL);


        add(tabs, top, grid);
    }

    private static Renderer<BoardListItemView> createBoardRenderer() {
        return LitRenderer.<BoardListItemView>of(NAME_AND_ABBREVIATION_TEMPLATE)
                .withProperty("name", BoardListItemView::name)
                .withProperty("abbreviation", BoardListItemView::abbreviation);
    }

    private TextField prepareSearchField() {
        var searchField = new TextField();
        searchField.setWidth("25%");
        searchField.setPlaceholder("Najit");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        return searchField;
    }

    private static final SerializableBiConsumer<Span, BoardListItemView> statusComponentUpdater = (span, board) -> {
        boolean isActive = board.active();
        setupStateSpan(span, isActive);
    };


    private static ComponentRenderer<Span, BoardListItemView> createStatusComponentRenderer() {
        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }
}
