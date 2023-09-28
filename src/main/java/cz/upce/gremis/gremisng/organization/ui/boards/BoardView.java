package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.organization.ui.boards.detail.CommitteeFormDialog;
import cz.upce.gremis.gremisng.security.SecurityService;
import cz.upce.gremis.gremisng.ui.AsyncDataCallback;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.ui.components.EmptyPanel;
import cz.upce.gremis.gremisng.ui.components.NotificationHelper;
import cz.upce.gremis.gremisng.ui.components.UiTips;
import jakarta.annotation.security.PermitAll;
import lombok.extern.log4j.Log4j2;
import org.vaadin.firitin.components.grid.VGrid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Route(value = "/boardnew", layout = MainLayout.class)
@PermitAll
@Log4j2
public class BoardView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private final BoardViewLogic boardViewLogic = new BoardViewLogic(this);

    private final BoardViewData viewData;

    private final H1 title;

    private final BoardMenuBar boardMenuBar;

    private final BoardDetail boardDetail;

    private final TabSheet tabSheet;

    private Board board;

    private Long boardId;

    private Long selectedTerm = null;

    BoardView(SecurityService securityService,
              BoardViewData viewData) {
        this.viewData = viewData;

        // page top
        title = new H1();
        boardMenuBar = new BoardMenuBar(boardViewLogic);
        var pageTop = createPageTop(title, boardMenuBar);
        add(pageTop);

        boardDetail = new BoardDetail(boardViewLogic, viewData);
        var lazyTerms = new LazyTerms();


        tabSheet = createTabSheet(boardDetail, lazyTerms);

        add(tabSheet);

        boardViewLogic.init(securityService.getAuthenticatedUserDetails());
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.boardId = parameter;
        var queryParameters = event.getLocation().getQueryParameters().getParameters();
        var termHolder = queryParameters.get("term");
        if (termHolder != null && !termHolder.isEmpty()) {
            selectedTerm = Long.parseLong(termHolder.get(0));
        }

        viewData.getBoard(boardId, new AsyncDataCallback<>() {
            @Override
            public void operationFinished(Board data) {
                accessUi(() -> {
                    log.info("Board loaded");
                    boardViewLogic.enter(data);
                });
            }

            @Override
            public void operationFailed(Throwable error) {
                accessUi(() -> NotificationHelper.showError(error));
            }
        });

        if (selectedTerm == null) {
            viewData.getCurrentTerm(boardId, new AsyncDataCallback<>() {
                @Override
                public void operationFinished(Optional<TermDto> data) {
                    accessUi(() -> data.ifPresentOrElse(termDto -> {
                        selectedTerm = termDto.id();
                        boardDetail.setTerm(termDto);
                    }, () -> boardDetail.setTermVisible(false)));
                }

                @Override
                public void operationFailed(Throwable error) {
                    accessUi(() -> NotificationHelper.showError(error));
                }
            });
        }
    }

    private static Component createPageTop(H1 title, BoardMenuBar menuBar) {
        var pageTop = new HorizontalLayout();
        pageTop.setWidthFull();
        pageTop.setAlignItems(Alignment.CENTER);

        pageTop.addAndExpand(title);
        pageTop.add(menuBar);

        return pageTop;
    }

    private TabSheet createTabSheet(BoardDetail boardDetail, LazyTerms lazyTerms) {
        var tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.add("Detail", boardDetail);
        tabSheet.add("Všechna funkční období", lazyTerms);
        tabSheet.add("Funkce", new LazySeats());
        tabSheet.add("Komise", new LazyCommittees());

        return tabSheet;
    }

    @Override
    public String getPageTitle() {
        return "Grémium ...";
    }


    private void setTitle(String title) {
        this.title.setText(title);
        getUI().map(UI::getPage).ifPresent(page -> {
            page.setTitle("Grémium " + title);
        });
    }

    void showEditDialog() {
        var dialog = new EditBoardDialog();
        dialog.setBoard(board);
        dialog.addOpenedChangeListener(event -> boardViewLogic.updateTitle(event.isOpened()));
        dialog.addSaveListener(e -> viewData.save(board)
                .thenAccept(v -> accessUi(() -> {
                    boardViewLogic.refresh(board);
                    NotificationHelper.showSuccess("Grémium uloženo");
                    dialog.close();
                }))
                .exceptionally(error -> {
                    NotificationHelper.showError(error);
                    return null;
                })
        );

        dialog.open();
    }

    void setBoard(Board board) {
        this.board = board;
        boardDetail.setBoard(board);
        setTitle(board.getName());
    }

    void setCurrentTerm(TermDto termDto) {
        selectedTerm = termDto.id();
        boardDetail.setTerm(termDto);
    }

    public void disableEditing() {
        boardMenuBar.disableEditing();
    }

    void setCommitteesEnable(boolean enabled) {
        tabSheet.getTabAt(3).setEnabled(enabled);
        boardMenuBar.setCommitteeEnabled(enabled);
        //disable/hide menu item
    }

    void setPerRollamEnabled(boolean enabled) {
        boardDetail.setVotingVisible(enabled);
        boardMenuBar.setPerRollamEnabled(enabled);
    }

    private void accessUi(Command command) {
        getUI().ifPresent(ui -> ui.access(command));
    }

    // TODO consider moving and refactoring term dialog
    public void openTermDialog() {
        var term = new BoardTerm();
        term.setBoard(board);
        boardDetail.openTermDialog(term, this.viewData);
    }

    public void openCommitteeDialog() {
        var committee = new Committee();

        var dialog = new CommitteeFormDialog(this.boardViewLogic);
        dialog.open();
        dialog.setCommittee(committee);
        dialog.addOnSaveClick(() -> {
            if (dialog.writeBeanIfValid(committee)) {
                committee.setBoard(board);
                viewData.save(committee, new UiAsyncDataCallback<>(savedCommittee -> {
                    NotificationHelper.showSuccess("Komise uložena");
                    dialog.close();
                }));
            }
        });
    }

    private class LazyTerms extends Div {

        private Grid<TermDto> grid;

        private EmptyPanel emptyListPanel;

        LazyTerms() {
            addAttachListener(attachEvent -> {
                log.debug("Terms attached");

                configureGrid();
                grid.setVisible(false);

                var layout = new VerticalLayout();
                layout.addAndExpand(grid);
                add(layout);

                viewData.getTerms(boardId, new AsyncDataCallback<>() {
                    @Override
                    public void operationFinished(List<TermDto> data) {
                        accessUi(() -> {
                            if (!data.isEmpty()) {
                                grid.setVisible(true);
                                grid.setItems(data);
                                grid.getListDataView()
                                        .setSortComparator((SerializableComparator<TermDto>) (o1, o2) -> o1.id().compareTo(o2.id()));
                            } else {
                                emptyListPanel = new EmptyPanel("Nebyla nalezena žádná funkční období. ", "Přidat funkční období");
                                add(emptyListPanel);
                            }
                        });
                    }

                    @Override
                    public void operationFailed(Throwable error) {
                        accessUi(() -> NotificationHelper.showError(error));
                    }
                });

                boardDetail.addSaveListener(e -> update(e.getValue()));
            });
        }

        private void configureGrid() {
            grid = new Grid<>();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.setSelectionMode(Grid.SelectionMode.NONE);

            grid.addColumn(
                    new ComponentRenderer<>(Span::new, (span, term) -> {
                        if (term.id().equals(selectedTerm)) {
                            span.add(new Icon("lumo", "checkmark"));
                        }
                    })).setWidth("2em").setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);
            grid.addColumn(TermDto::description).setHeader("Popis");
            grid.addColumn(TermDto::start).setHeader("Začátek");
            grid.addColumn(TermDto::end).setHeader("Konec");

            grid.addColumn(LitRenderer.<TermDto>of("<vaadin-button theme='tertiary' @click=${selectTerm}>Vybrat</vaadin-button>")
                    .withFunction("selectTerm", term -> {
                        boardViewLogic.selectTerm(term);
                        grid.getListDataView().refreshAll();
                        NotificationHelper.showInfo("Funkční období změněno");
                    }));
        }

        public void update(TermDto value) {
            grid.getListDataView().getItems()
                    .filter(it -> it.id().equals(value.id()))
                    .findFirst()
                    .ifPresent(it -> grid.getListDataView().removeItem(it));
            grid.getListDataView().addItem(value);

            if (emptyListPanel != null) {
                emptyListPanel.setVisible(false);
            }
        }
    }

    private class LazySeats extends Div {

        private BoardSeat draggedItem;
        private GridListDataView<BoardSeat> seatsDataView;

        LazySeats() {
            addAttachListener(e -> {
                var layout = new VerticalLayout();

                var grid = new VGrid<BoardSeat>()
                        .withSelectionMode(Grid.SelectionMode.NONE)
                        .withThemeVariants(GridVariant.LUMO_NO_BORDER);

                grid.addColumn(new ComponentRenderer<>(seat -> new Avatar(String.valueOf(seat.getOrder()))))
                        .setHeader("Pořadí")
                        .setTextAlign(ColumnTextAlign.CENTER)
                        .setWidth("5em")
                        .setFlexGrow(0)
                        .setResizable(false);

                grid.addColumn(BoardSeat::getName).setHeader("Název");
                grid.addColumn(BoardSeat::getDuration)
                        .setTextAlign(ColumnTextAlign.CENTER)
                        .setHeader("Doba trvnání v měsících");
                grid.addColumn(seat -> seat.getSettings().getFilling()).setHeader("Povinnost");
                grid.addColumn(seat -> seat.getSettings().getGrantedRole()).setHeader("Role");
                grid.addColumn(seat -> seat.getSettings().getMinMemberCount())
                        .setTextAlign(ColumnTextAlign.CENTER)
                        .setHeader("Min. počet členů");

                //drag & drop
                grid.setDropMode(GridDropMode.BETWEEN);
                grid.setRowsDraggable(true);
                grid.addDragStartListener(dragStartEvent -> draggedItem = dragStartEvent.getDraggedItems().get(0));
                grid.addDropListener(dragEndEvent -> {
                    GridDropLocation dropLocation = dragEndEvent.getDropLocation();
                    BoardSeat targetItem = dragEndEvent.getDropTargetItem().orElse(null);

                    boolean personWasDroppedOntoItself = draggedItem
                            .equals(targetItem);

                    if (targetItem == null || personWasDroppedOntoItself)
                        return;

                    seatsDataView.removeItem(draggedItem);

                    if (dropLocation == GridDropLocation.BELOW) {
                        seatsDataView.addItemAfter(draggedItem, targetItem);
                    } else {
                        seatsDataView.addItemBefore(draggedItem, targetItem);
                    }

                });
                grid.addDragEndListener(dragEnd -> {
                    draggedItem = null;
                    if (seatsDataView.getItemCount() > 1) {
                        var iterator = seatsDataView.getItems().iterator();
                        var order = 1;
                        while (iterator.hasNext()) {
                            var seat = iterator.next();
                            seat.setOrder(order++);
                        }

                        BoardView.this.viewData.updateSeats(boardId, seatsDataView.getItems().toList(),
                                new UiAsyncDataCallback<>(done -> NotificationHelper.showInfo("Pořadí funkcí změněno")));
                    }
                });

                UiTips.addTip(layout, "Pořadí funkcí lze změnit přetažením");
                layout.add(grid);
                add(layout);

                BoardView.this.viewData.getSeats(boardId,
                        new UiAsyncDataCallback<>(seats -> seatsDataView = grid.setItems(new ArrayList<>(seats))));
            });
        }
    }

    private static class LazyCommittees extends Div {

        public LazyCommittees() {
            addAttachListener(e -> {
                log.debug("Seats attached");
                add(new VerticalLayout(new Span("Committees")));
            });
        }
    }

    class UiAsyncDataCallback<T> implements AsyncDataCallback<T>, Serializable {

        private final SerializableConsumer<T> dataConsumer;


        UiAsyncDataCallback(SerializableConsumer<T> dataConsumer) {
            this.dataConsumer = dataConsumer;
        }

        @Override
        public void operationFinished(T data) {
            accessUi(() -> dataConsumer.accept(data));
        }

        @Override
        public void operationFailed(Throwable error) {
            log.info("Showing error");
            accessUi(() -> NotificationHelper.showError(error)); // Dialog?
        }
    }
}
