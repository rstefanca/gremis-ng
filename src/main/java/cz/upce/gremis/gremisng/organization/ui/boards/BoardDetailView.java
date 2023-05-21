package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.assignments.domain.BoardSeatAssignment;
import cz.upce.gremis.gremisng.assignments.service.BoardSeatAssignmentService;
import cz.upce.gremis.gremisng.organization.domain.Board;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import cz.upce.gremis.gremisng.organization.domain.Committee;
import cz.upce.gremis.gremisng.organization.service.BoardAggregateService;
import cz.upce.gremis.gremisng.organization.ui.committees.CommitteeDetailView;
import cz.upce.gremis.gremisng.organization.ui.committees.CommitteeDialog;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.ui.components.AssignmentDialog;
import cz.upce.gremis.gremisng.ui.components.ConfirmDeleteDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static cz.upce.gremis.gremisng.ui.components.SpanHelpers.setupStateSpan;

@Route(value = "/board", layout = MainLayout.class)
public class BoardDetailView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {


    private final LazyTabs lazyTabs = new LazyTabs();

    private final BoardAggregateService service;
    private final BoardSeatAssignmentService assignmentService;

    private BoardForm boardForm;
    private ComboBox<BoardTerm> termSelector = new ComboBox<>();

    private Grid<BoardTerm> termsGrid = new Grid<>();
    private GridListDataView<BoardTerm> termsDataView;

    Span termsBadge = createBadge(0);

    private Tab committeeTab;

    private LazyAssignments assignmentsComponent = new LazyAssignments();

    private final Span titleSpan = new Span();

    //Dialogs
    private final Dialog confirmDeleteDialog;
    private final AssignmentDialog assignmentDialog;

    private BoardTermDialog boardTermDialog;

    private CommitteeDialog committeeDialog;

    private SeatDialog seatDialog;

    private Board board;

    // TODO cleanup constructor
    BoardDetailView(BoardAggregateService service,
                    BoardSeatAssignmentService assignmentService,
                    AssignmentDialog assignmentDialog) {
        this.service = service;
        this.assignmentService = assignmentService;
        this.assignmentDialog = assignmentDialog;

        confirmDeleteDialog = confirmDeleteDialog();

        var top = new HorizontalLayout();
        setupTop(top);


        setupTermsGrid();

        boardForm = new BoardForm();
        //boardForm.setReadOnly(true);

        var detailLayout = new VerticalLayout(boardForm);

        var saveButton = new Button(getTranslation("buttons.save"));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        detailLayout.add(saveButton);

        termSelector.setItemLabelGenerator(BoardTerm::getDescription);
        termSelector.setRenderer(createRenderer());
        termSelector.setMinWidth("225px");
        termSelector.setMaxWidth("300px");

        detailLayout.add(termSelector);

        var assignedPersonsDetails = new Details("Osoby ve funkcích", assignmentsComponent);
        assignedPersonsDetails.setOpened(false);
        assignedPersonsDetails.setWidthFull();
        detailLayout.add(assignedPersonsDetails);

        var votingsDetails = new Details("Hlasováni", new Text("K dispozici nejsou žádné hlasování."));
        votingsDetails.setOpened(false);
        detailLayout.add(votingsDetails);

        var meetingsDetails = new Details("Zasedání", new Span("K dispozici nejsou žádné zasedání."));
        meetingsDetails.setOpened(false);
        detailLayout.add(meetingsDetails);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();

        tabSheet.add("Detail", detailLayout);

        var termsTabLayout = new VerticalLayout(termsGrid);
        termsTabLayout.setWidthFull();

        var termsTab = tabSheet.add(new Div(new Span("Všechna funkční období"), termsBadge), termsTabLayout);

        tabSheet.add(new Div(new Span("Funkce"), createBadge(2)), new LazySeats());

        createMenu(tabSheet);

        committeeTab = tabSheet.add(new Div(new Span("Komise"), createBadge(0)),
                new LazyCommittees());

        committeeTab.setEnabled(false);

        add(tabSheet);


        add(top, tabSheet);
    }

    private void setupTop(HorizontalLayout top) {
        top.setWidthFull();
        top.setAlignItems(Alignment.END);

        titleSpan.setClassName(LumoUtility.FontSize.LARGE);
        titleSpan.setWidthFull();

        top.add(titleSpan);
    }

    private void setupTermsGrid() {
        termsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        termsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        termsGrid.addColumn(BoardTerm::getDescription).setHeader("Popis");
        termsGrid.addColumn(BoardTerm::getStart).setHeader("Začátek");
        termsGrid.addColumn(BoardTerm::getEnd).setHeader("Konec");
        termsGrid.addColumn(
                new ComponentRenderer<>(Div::new, (div, term) -> {
                    var editButton = new Button();
                    editButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
                    editButton.setIcon(new Icon(VaadinIcon.EDIT));
                    editButton.addClickListener(e -> {
                        if (boardTermDialog == null) {
                            initTermDialog();
                        }
                        boardTermDialog.setItem(term);
                        boardTermDialog.open();
                    });

                    var removeButton = new Button();
                    removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
                    removeButton.setIcon(new Icon(VaadinIcon.TRASH));
                    //TODO add action
                    removeButton.addClickListener(e -> showConfirmDeleteTermDialog(confirmEvent -> {
                        if (board.remove(term)) {
                            service.remove(term);
                            termsDataView.removeItem(term);
                            Notification.show("Smazáno funknčí období");
                        }
                    }));
                    div.add(editButton, removeButton);

                })).setWidth("12em").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
    }

    private void initTermDialog() {
        boardTermDialog = new BoardTermDialog();
        boardTermDialog.setOnSave(it -> {
            var isNew = it.isNew();
            if (isNew) {
                addTerm(it);
            } else {
                saveBoard();
                termsDataView.refreshItem(it);
            }

            Notification.show("Funkční období uloženo");
        });
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        service.getBoard(parameter, board -> {
            this.board = board;
            boardForm.setBoard(board);
            var terms = board.getTerms();
            termSelector.setItems(terms);
            termsDataView = termsGrid.setItems(terms);
            termsBadge.setText(String.valueOf(terms.size()));
            //select actual term
            terms.stream().findFirst().ifPresent(t -> termSelector.setValue(t));
            committeeTab.setEnabled(board.isCommitteesEnabled());
            titleSpan.setText(String.format("%s - %s", board.getName(), termSelector.getValue().getDescription()));
        });
    }

    private void createMenu(TabSheet parent) {

        var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);

        var createMenuItem = menuBar.addItem("Přidat");
        var subMenu = createMenuItem.getSubMenu();
        var newTermMenuItem = subMenu.addItem("Funkční období");

        newTermMenuItem.addClickListener(e -> {
            if (boardTermDialog == null) {
                initTermDialog();
            }
            boardTermDialog.setItem(new BoardTerm());
            boardTermDialog.open();
        });

        var newSeatMenuItem = subMenu.addItem("Funkci");
        newSeatMenuItem.addClickListener(e -> {
            if (seatDialog == null) {
                seatDialog = new SeatDialog();
            }
            seatDialog.open();
        });

        var newCommitteeMenuItem = subMenu.addItem("Komisi");
        newCommitteeMenuItem.addClickListener(e -> openCommitteeDialog());
        //newCommitteeMenuItem.setEnabled(board.isCommitteesEnabled()); //TODO respond to some event when settings change

        var newAdmin = subMenu.addItem("Administrátora");
        newAdmin.addClickListener(e -> {
            Notification.show("TODO", 2300, Notification.Position.MIDDLE);
        });


        var deleteItem = menuBar.addItem(new Icon(VaadinIcon.TRASH));
        deleteItem.add(getTranslation("buttons.remove"));
        deleteItem.addClickListener(e -> this.openDeleteDialog());

        var menuDiv = new Div(menuBar);
        menuDiv.setWidthFull();

        parent.setSuffixComponent(menuBar);
    }

    private void openCommitteeDialog() {
        if (committeeDialog == null) {
            configureCommitteeDialog();
        }
        committeeDialog.setCommittee(new Committee());
        committeeDialog.open();
    }

    private void configureCommitteeDialog() {
        committeeDialog = new CommitteeDialog();
        committeeDialog.getForm().addCloseListener(e -> closeCommitteeDialog());
        committeeDialog.getForm().addSaveListener(e -> saveCommittee(e.getCommittee()));
    }

    private void closeCommitteeDialog() {
        committeeDialog.setCommittee(null);
        committeeDialog.close();
    }

    private void openDeleteDialog() {
        confirmDeleteDialog.open();
        confirmDeleteDialog.setHeaderTitle(
                "Odstranit " + boardForm.getBoard().getName() + "?");
    }

    private static Renderer<BoardTerm> createRenderer() {
        String template = """
                <div style="display: flex;">
                    <div>
                        ${item.description}
                        <div style=\\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\\">${item.from} - ${item.to}</div>
                    </div>
                </div>""";

        return LitRenderer.<BoardTerm>of(template)
                .withProperty("description", BoardTerm::getDescription)
                .withProperty("from", boardTerm -> boardTerm.getStart().toString())
                .withProperty("to", boardTerm -> boardTerm.getEnd().toString());
    }

    @Deprecated
    private Dialog confirmDeleteDialog() {
        Dialog dialog = new Dialog();

        dialog.add("Opravdu chcete smazat gremium");

        Button deleteButton = new Button(getTranslation("buttons.remove"), (e) -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);

        Button cancelButton = new Button(getTranslation("buttons.cancel"), (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);

        return dialog;
    }

    private void saveBoard() {
        board = service.save(board);
        termsDataView = termsGrid.setItems(board.getTerms());
    }

    private void addTerm(BoardTerm boardTerm) {
        board.add(boardTerm);
        board = service.save(board);
        termsDataView = termsGrid.setItems(board.getTerms());
    }

    private void showConfirmDeleteTermDialog(ComponentEventListener<ConfirmDialog.ConfirmEvent> confirmListener) {
        var dialog = new ConfirmDeleteDialog("Opravdu chcete smazat funkcni obodbi?");
        dialog.open();
        dialog.addConfirmListener(confirmListener);
    }

    /**
     * Helper method for creating a badge.
     */
    private static Span createBadge(int value) {
        Span badge = new Span(String.valueOf(value));
        badge.getElement().getThemeList().add("badge small contrast");
        badge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
        return badge;
    }

    @Override
    public String getPageTitle() {
        return (board != null) ? board.getName() : "Grémium";
    }

    private class LazySeats extends Div {

        private BoardSeat draggedItem;
        private GridListDataView<BoardSeat> dataView;

        LazySeats() {
            addAttachListener(e -> {
                var layout = new VerticalLayout();
                var seatGrid = new Grid<BoardSeat>();
                seatGrid.setSelectionMode(Grid.SelectionMode.NONE);
                seatGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
                seatGrid.addColumn(new ComponentRenderer<>(seat -> new Avatar(String.valueOf(seat.getOrder()))))
                        .setHeader("Pořadí")
                        .setTextAlign(ColumnTextAlign.CENTER)
                        .setWidth("5em")
                        .setFlexGrow(0)
                        .setResizable(false);

                seatGrid.addColumn(BoardSeat::getName).setHeader("Název");
                seatGrid.addColumn(BoardSeat::getDuration)
                        .setTextAlign(ColumnTextAlign.CENTER)
                        .setHeader("Doba trvnání v měsících");
                seatGrid.addColumn(seat -> seat.getSettings().getFilling()).setHeader("Povinnost");
                seatGrid.addColumn(seat -> seat.getSettings().getGrantedRole()).setHeader("Role");
                seatGrid.addColumn(
                        new ComponentRenderer<>(Div::new, (div, boardSeat) -> {
                            var addButton = new Button();
                            addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
                            addButton.addClickListener(clickEvent -> {
                                assignmentDialog.setSelectedTerm(termSelector.getValue());
                                assignmentDialog.setSelectedSeat(boardSeat);
                                assignmentDialog.open();
                            });
                            addButton.setIcon(new Icon(VaadinIcon.PLUS));
                            addButton.setTooltipText("Přídat osobu");
                            div.add(addButton);

                            var removeButton = new Button();
                            removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
                            //TODO add action
                            removeButton.setIcon(new Icon(VaadinIcon.TRASH));
                            div.add(removeButton);

                        })).setWidth("12em").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

                //drag & drop
                seatGrid.setDropMode(GridDropMode.BETWEEN);
                seatGrid.setRowsDraggable(true);
                seatGrid.addDragStartListener(dragStartEvent -> draggedItem = dragStartEvent.getDraggedItems().get(0));
                seatGrid.addDropListener(dragEndEvent -> {
                    GridDropLocation dropLocation = dragEndEvent.getDropLocation();
                    BoardSeat targetItem = dragEndEvent.getDropTargetItem().orElse(null);

                    boolean personWasDroppedOntoItself = draggedItem
                            .equals(targetItem);

                    if (targetItem == null || personWasDroppedOntoItself)
                        return;

                    dataView.removeItem(draggedItem);

                    if (dropLocation == GridDropLocation.BELOW) {
                        dataView.addItemAfter(draggedItem, targetItem);
                    } else {
                        dataView.addItemBefore(draggedItem, targetItem);
                    }

                });
                seatGrid.addDragEndListener(dragEnd -> {
                    draggedItem = null;
                    if (dataView.getItemCount() > 1) {
                        var iterator = dataView.getItems().iterator();
                        var order = 1;
                        while (iterator.hasNext()) {
                            var seat = iterator.next();
                            seat.setOrder(order++);
                        }
                        service.updateSeats(board.getId(), dataView.getItems().toList());

                        Notification.show("Pořadí funkcí změněno");
                    }
                });


                addTip(layout);
                layout.add(seatGrid);
                add(layout);

                assignmentDialog.addDialogCloseActionListener(closeEvent -> {
                    assignmentsComponent.refresh();
                });

                service.getBoardSeats(board.getId(), seats -> {
                    dataView = seatGrid.setItems(new ArrayList<>(seats.toList()));
                });
            });
        }

        private void addTip(HasComponents parent) {
            var icon = new Icon(VaadinIcon.LIGHTBULB);
            icon.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.TERTIARY);
            var text = new Span("Pořadí funkcí lze změnit přetažením");
            text.addClassNames(LumoUtility.Margin.Left.SMALL, LumoUtility.TextColor.TERTIARY);
            var div = new Div(
                    icon,
                    text);
            parent.add(div);
        }
    }

    private void saveCommittee(Committee committee) {
        if (committee.isNew()) {
            committee.setBoard(board);
        }
        service.saveCommittee(committee);
        committeeDialog.close();

        lazyTabs.get(LazyCommittees.class)
                .ifPresent(LazyCommittees::refresh);

        Notification.show("Komise ulozena");
    }

    private class LazyCommittees extends Div implements LazyTab {

        private boolean loaded = false;
        private final Grid<Committee> committeeGrid = new Grid<>();

        public LazyCommittees() {
            addAttachListener(e -> {
                var layout = new VerticalLayout();
                layout.setWidthFull();

                committeeGrid.setSelectionMode(Grid.SelectionMode.NONE);
                committeeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
                committeeGrid.addColumn(new ComponentRenderer<>(committee -> new Avatar(String.valueOf(committee.getOrder()))))
                        .setHeader("Pořadí")
                        .setTextAlign(ColumnTextAlign.CENTER)
                        .setWidth("5em")
                        .setFlexGrow(0)
                        .setResizable(false);
                committeeGrid.addColumn(createCommitteeRenderer()).setHeader("Název");
                committeeGrid.addColumn(Committee::getMinimumSeats).setHeader("Min. počet členů").setTextAlign(ColumnTextAlign.CENTER);
                committeeGrid.addColumn(Committee::getTermDurationInMonths).setHeader("Doba v měsících").setTextAlign(ColumnTextAlign.CENTER);
                committeeGrid.addColumn(createStatusComponentRenderer()).setHeader("Stav").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

                committeeGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, committee) -> {
                            var detailButton = new Button();
                            detailButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_TERTIARY);
                            detailButton.setIcon(new Icon(VaadinIcon.EXTERNAL_LINK));
                            detailButton.setTooltipText("Přejít na detail komise");
                            detailButton.addClickListener(click ->
                                    getUI().ifPresent(ui -> ui.navigate(CommitteeDetailView.class, committee.getId())));

                            span.add(detailButton);

                            var removeButton = new Button();
                            removeButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_ERROR,
                                    ButtonVariant.LUMO_TERTIARY);
                            //removeButton.addClickListener(clickEvent -> );
                            removeButton.setIcon(new Icon(VaadinIcon.TRASH));
                            span.add(removeButton);
                            span.addClassNames(LumoUtility.AlignContent.END);


                        })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);


                layout.add(committeeGrid);

                service.getCommittees(board.getId(), committees -> committeeGrid.setItems(committees.toList()));
                loaded = true;

                add(layout);
                lazyTabs.add(this);
            });
        }

        void refresh() {
            if (loaded) {
                service.getCommittees(board.getId(), committees -> committeeGrid.setItems(committees.toList()));
            }
        }

        private static Renderer<Committee> createCommitteeRenderer() {
            return RendererTemplates.createNameAndAbbreviationRenderer(Committee::getName, Committee::getAbbreviation);
        }

        private static ComponentRenderer<Span, Committee> createStatusComponentRenderer() {
            return new ComponentRenderer<>(Span::new, statusComponentUpdater);
        }

        private static final SerializableBiConsumer<Span, Committee> statusComponentUpdater = (span, committee) -> {
            boolean isActive = committee.isActive();
            setupStateSpan(span, isActive);
        };
    }

    // TODO - not lazy yet
    private class LazyAssignments extends Div {

        private final Grid<BoardSeatAssignment> grid = new Grid<>();
        private GridLazyDataView<BoardSeatAssignment> lazyDataView;

        LazyAssignments() {
            addAttachListener(e -> {
                var layout = new VerticalLayout();

                grid.setSelectionMode(Grid.SelectionMode.NONE);
                grid.setPageSize(15);

                // setup grid
                grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
                var firstColumn = grid.addColumn(it -> it.getPerson().getFullName())
                        .setAutoWidth(true)
                        .setHeader("Osoba")
                        .setComparator(it -> it.getPerson().getLastName());

                grid.addColumn(it -> it.getSeat().getName()).setHeader("Funkce v grémiu")
                        .setSortable(true);
                grid.addColumn(BoardSeatAssignment::getStart).setHeader("Ve funkci od")
                        .setSortable(true);
                grid.addColumn(BoardSeatAssignment::getEnd).setHeader("Ve funkci do");

                grid.addColumn(
                        new ComponentRenderer<>(Button::new, (button, person) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_ERROR,
                                    ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(clickEvent -> this.removeAssignments(person));
                            button.setIcon(new Icon(VaadinIcon.TRASH));
                        })).setWidth("6em").setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

                grid.setMaxHeight("500px");

                lazyDataView = grid.setItems(q -> {
                    var termId = termSelector.getValue().getId();
                    // TODO - return dto (flat?) instead of entity
                    var page = assignmentService.findByTerm_Id(termId, VaadinSpringDataHelpers.toSpringPageRequest(q));
                    firstColumn.setFooter(String.format("%s osob", page.getTotalElements()));
                    return page.stream();
                });

                layout.add(grid);
                add(layout);

                termSelector.addValueChangeListener(valueChangeEvent -> refresh());
            });
        }

        void refresh() {
            lazyDataView.refreshAll();
        }

        private void removeAssignments(BoardSeatAssignment seatAssignment) {


        }

        private void setItems(Collection<BoardSeatAssignment> items) {
            grid.setItems(items);
        }

    }

    /**
     * Marker
     */
    private interface LazyTab {
    }

    private static class LazyTabs {
        private final ArrayList<LazyTab> containers = new ArrayList<>();

        void add(LazyTab container) {
            containers.add(container);
        }

        <T extends LazyTab> Optional<T> get(Class<T> ofType) {
            return containers.stream()
                    .filter(it -> it.getClass().equals(ofType))
                    .map(ofType::cast)
                    .findFirst();
        }
    }
}
