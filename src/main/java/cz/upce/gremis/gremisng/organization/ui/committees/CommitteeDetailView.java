package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.common.GremisRole;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.organization.service.CommitteeAggregateService;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.ui.components.ConfirmDeleteDialog;
import cz.upce.gremis.gremisng.ui.components.EmptyListPanel;
import cz.upce.gremis.gremisng.ui.components.UiTips;

import java.util.ArrayList;


@Route(value = "/committee", layout = MainLayout.class)
public class CommitteeDetailView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private final CommitteeAggregateService service;

    private final CommitteeTermDialog committeeTermDialog;

    private final CommitteeAssignmentDialog assignmentDialog;

    private final CommitteeAssignmentsGrid committeeAssignmentsGrid;

    private final CommitteeSeatDialog committeeSeatDialog = new CommitteeSeatDialog();

    private final WorkingGroupDialog workingGroupDialog;

    private final CommitteeMenuBar committeeMenuBar = new CommitteeMenuBar();


    private final Span titleSpan = new Span();

    private final TabSheet tabSheet = new TabSheet();

    private final CommitteeForm committeeForm = new CommitteeForm();

    private Committee committee;

    private Long selectedTermId;

    public CommitteeDetailView(CommitteeAggregateService service,
                               CommitteeTermDialog committeeTermDialog,
                               CommitteeAssignmentDialog assignmentDialog,
                               CommitteeAssignmentsGrid committeeAssignmentsGrid,
                               WorkingGroupDialog workingGroupDialog) {
        this.service = service;
        this.committeeTermDialog = committeeTermDialog;
        this.assignmentDialog = assignmentDialog;
        this.committeeAssignmentsGrid = committeeAssignmentsGrid;
        this.workingGroupDialog = workingGroupDialog;

        setWidthFull();
        setHeightFull();

        configureTop();
        configureTabs();
        addDetailTab();
        addTermsTab();
        configureMenu();
        addSeatsTab();
    }

    private void configureTop() {
        var top = new HorizontalLayout();
        top.setWidthFull();
        top.setAlignItems(Alignment.END);

        titleSpan.setClassName(LumoUtility.FontSize.LARGE);
        titleSpan.setWidthFull();

        top.add(titleSpan);

        add(top);
    }

    private void configureTabs() {
        tabSheet.setWidthFull();
        add(tabSheet);
    }

    private void configureMenu() {
        committeeMenuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED);
        committeeMenuBar.addAttachListener(e -> {
            committeeMenuBar.setWorkingGroupEnabled(committee.isWorkingGroupsEnabled());
        });

        var menuDiv = new Div(committeeMenuBar);
        menuDiv.setWidthFull();

        tabSheet.setSuffixComponent(committeeMenuBar);
    }

    private void addDetailTab() {
        var detailLayout = new VerticalLayout();
        var formLayout = new VerticalLayout();
        formLayout.add(committeeForm);
        detailLayout.add(formLayout);

        var assignmentsDetails = new Details("Osoby ve funkcích", committeeAssignmentsGrid);
        assignmentsDetails.setWidthFull();
        assignmentsDetails.addOpenedChangeListener(e -> {
            if (e.isOpened()) {
                committeeAssignmentsGrid.init();
            }
        });

        detailLayout.add(assignmentsDetails);

        var workingGroupDetails = new Details("Pracovní skupiny");
        workingGroupDetails.addAttachListener(e -> {
            workingGroupDetails.setEnabled(committee.isWorkingGroupsEnabled());
        });
        detailLayout.add(workingGroupDetails);

        tabSheet.add("Detail", detailLayout);
    }

    private void addTermsTab() {
        var content = new LazyTerms();
        var tab = tabSheet.add("Všechna funkční období", content);
        content.setTab(tab);
    }

    private void addSeatsTab() {
        var content = new LazySeats();
        tabSheet.add("Funkce", content);
        //content.setTab(tab);
    }

    @Override
    public String getPageTitle() {
        return committee.getName();
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        committee = service.getCommittee(new CommitteeId(parameter));
        committeeForm.setItem(committee);
        assignmentDialog.setCommitteeId(committee.getId());
        //committeeMenuBar.setWorkingGroupEnabled(committee.isWorkingGroupsEnabled());

        updateTitle();
    }

    private void updateTitle() {
        titleSpan.setText(committee.getName());
    }

    private void showConfirmDeleteTermDialog(String title, ComponentEventListener<ConfirmDialog.ConfirmEvent> confirmListener) {
        var dialog = new ConfirmDeleteDialog(title);
        dialog.open();
        dialog.addConfirmListener(confirmListener);
    }

    private void openTermDialog() {
        committeeTermDialog.setItem(new CommitteeTerm());
        committeeTermDialog.setBoardId(new BoardId(committee.getBoard().getId()));
        committeeTermDialog.open();
    }

    private void openTermDialog(CommitteeTerm term) {
        committeeTermDialog.setItem(term);
        committeeTermDialog.setBoardId(new BoardId(committee.getBoard().getId()));
        committeeTermDialog.open();
    }

    private void openSeatDialog() {
        committeeSeatDialog.setItem(new CommitteeSeat());
        committeeSeatDialog.open();
    }

    private class LazyTerms extends Div {

        private final Grid<TermDto> grid = new Grid<>();

        private GridListDataView<TermDto> dataView;

        private Tab tab;

        private EmptyListPanel emptyListPanel;

        LazyTerms() {
            addAttachListener(attachEvent -> {
                var verticalLayout = new VerticalLayout();
                configureEmptyList(verticalLayout);
                configureGrid(verticalLayout);
                add(verticalLayout);

                committeeTermDialog.addSaveListener(e -> {
                    var term = e.getTerm();
                    saveTerm(term);
                    committeeTermDialog.close();
                    committeeTermDialog.setItem(null);
                    tabSheet.setSelectedTab(this.tab);
                    committeeAssignmentsGrid.setTermId(term.getId());
                });
                committeeTermDialog.addCloseListener(e -> committeeTermDialog.close());

                refresh();
            });
        }

        private void configureEmptyList(VerticalLayout verticalLayout) {
            emptyListPanel = new EmptyListPanel("Nebyla nalezena žádná funkční období. ", "Přidat funkční období");
            emptyListPanel.addActionClickListener(e -> openTermDialog());
            verticalLayout.add(emptyListPanel);
        }

        private void configureGrid(HasComponents parent) {
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addColumn(TermDto::description).setHeader("Popis");
            grid.addColumn(TermDto::start).setHeader("Začátek");
            grid.addColumn(TermDto::end).setHeader("Konec");
            //action buttons
            grid.addColumn(new ComponentRenderer<>(Div::new, (div, term) -> {
                var editButton = new Button();
                editButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
                editButton.setIcon(new Icon(VaadinIcon.EDIT));
                editButton.addClickListener(e -> {
                    var editedTerm = service.getTerm(term.id());
                    openTermDialog(editedTerm);
                });
                div.add(editButton);

                var removeButton = new Button();
                removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                removeButton.setIcon(new Icon(VaadinIcon.TRASH));

                removeButton.addClickListener(e -> showConfirmDeleteTermDialog("Opravdu chcete smazat funkční odbobí?", confirmEvent -> {
                    removeTerm(term);
                    Notification.show("Funkční odbobí smazáno");
                }));
                div.add(removeButton);
            })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);
            parent.add(grid);
        }

        private void refresh() {
            var terms = service.getTermDtos(new CommitteeId(committee.getId()));
            dataView = grid.setItems(terms);
            updateView();
        }

        private void saveTerm(CommitteeTerm term) {
            if (term.isNew()) {
                term.setCommittee(committee);
            }
            service.saveTerm(term);
            refresh();
            Notification.show("Saved");
        }

        private void removeTerm(TermDto termDto) {
            service.removeTerm(termDto.id());
            dataView.removeItem(termDto);
            updateView();
        }

        private void updateView() {
            var itemCount = dataView.getItemCount();
            var hasItems = itemCount > 0;
            grid.setVisible(hasItems);
            emptyListPanel.setVisible(!hasItems);
        }

        public void setTab(Tab tab) {
            this.tab = tab;
        }
    }

    private class LazySeats extends Div {

        private final Grid<SeatViewDecorator> grid = new Grid<>();

        private SeatViewDecorator draggedItem = null;

        private GridListDataView<SeatViewDecorator> dataView;

        private Div tip;

        private EmptyListPanel emptyListPanel;

        public LazySeats() {
            addAttachListener(attachEvent -> {
                var verticalLayout = new VerticalLayout();
                tip = UiTips.addTip(verticalLayout, "Pořadí funkcí lze změnit přetažením");
                tip.setVisible(false);

                emptyListPanel = new EmptyListPanel("Nebyly nalezeny žádné funkce. ", "Přidat funkci");
                emptyListPanel.addActionClickListener(e -> openSeatDialog());

                verticalLayout.add(emptyListPanel);

                configureGrid(verticalLayout);

                committeeSeatDialog.addCloseListener(e -> closeDialog());

                committeeSeatDialog.addSaveListener(e -> {
                    saveSeat(e.getSeat());
                    closeDialog();
                    Notification.show("Saved");
                    refresh();
                });

                add(verticalLayout);

                refresh();
            });
        }

        private void configureGrid(HasComponents parent) {
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);


            grid.addColumn(new ComponentRenderer<>(seat -> new Avatar(String.valueOf(seat.order))))
                    .setHeader("Pořadí")
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setWidth("5em")
                    .setFlexGrow(0)
                    .setResizable(false);

            grid.addColumn(SeatViewDecorator::name).setHeader("Název");
            grid.addColumn(SeatViewDecorator::filling).setHeader("Povinnost");
            grid.addColumn(SeatViewDecorator::grantedRole).setHeader("Role");

            //action buttons
            grid.addColumn(new ComponentRenderer<>(Div::new, (div, seat) -> {
                configureAddPersonButton(div, seat);
                configureRemoveButton(div, seat);
            })).setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.END);

            //drag & drop
            grid.setDropMode(GridDropMode.BETWEEN);
            grid.setRowsDraggable(true);
            grid.addDragStartListener(e -> draggedItem = e.getDraggedItems().get(0));
            grid.addDropListener(e -> {
                var dropLocation = e.getDropLocation();
                var targetItem = e.getDropTargetItem().orElse(null);

                boolean itemWasDroppedOntoItself = draggedItem.equals(targetItem);
                if (targetItem == null || itemWasDroppedOntoItself) {
                    return;
                }

                dataView.removeItem(draggedItem);

                if (dropLocation == GridDropLocation.BELOW) {
                    dataView.addItemAfter(draggedItem, targetItem);
                } else {
                    dataView.addItemBefore(draggedItem, targetItem);
                }
            });
            grid.addDragEndListener(e -> {
                draggedItem = null;
                if (dataView.getItemCount() > 1) {
                    updateSeatsOrder();
                }
            });

            parent.add(grid);
        }

        private void configureAddPersonButton(Div div, SeatViewDecorator seat) {
            var addButton = new Button();
            addButton.setIcon(new Icon(VaadinIcon.PLUS));
            addButton.setTooltipText("Přídat osobu");
            addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
            addButton.addClickListener(e -> {
                assignmentDialog.setSelectedSeat(seat.delegate);
                assignmentDialog.open();
            });
            div.add(addButton);
        }

        private void configureRemoveButton(Div div, SeatViewDecorator seat) {
            var removeButton = new Button();
            removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            removeButton.setIcon(new Icon(VaadinIcon.TRASH));
            removeButton.addClickListener(e -> showConfirmDeleteTermDialog("Opravdu chcete smazat funkci?", confirmEvent -> {
                removeSeat(seat);
                Notification.show("Funkce smazana");
            }));

            div.add(removeButton);
        }

        private void refresh() {
            var seats = service.getSeatDtos(new CommitteeId(committee.getId()));
            var decorated = seats.stream().map(SeatViewDecorator::new).toList();
            dataView = grid.setItems(new ArrayList<>(decorated));
            updateView();
        }

        private void saveSeat(CommitteeSeat seat) {
            if (seat.isNew()) {
                seat.setCommittee(committee);
            }
            service.saveSeat(seat);
        }

        private void removeSeat(SeatViewDecorator seat) {
            service.removeSeat(seat.id());
            var hasNext = dataView.getNextItem(seat).isPresent();
            dataView.removeItem(seat);
            if (hasNext) {
                updateSeatsOrder();
            }
            updateView();
        }

        private void updateView() {
            var itemCount = dataView.getItemCount();
            var hasItems = itemCount > 0;
            tip.setVisible(itemCount > 1);
            grid.setVisible(hasItems);
            emptyListPanel.setVisible(!hasItems);
        }

        private void updateSeatsOrder() {
            var iterator = dataView.getItems().iterator();
            var orderedIds = new ArrayList<Long>(dataView.getItemCount());
            var order = 1;
            while (iterator.hasNext()) {
                var seat = iterator.next();
                seat.order = order++;
                orderedIds.add(seat.id());
            }
            service.updateSeatsOrder(new CommitteeId(committee.getId()), orderedIds);
            dataView.refreshAll();
        }

        private void closeDialog() {
            committeeSeatDialog.setItem(null);
            committeeSeatDialog.close();
        }
    }

    private static class SeatViewDecorator {
        private final CommitteeSeatDto delegate;
        private int order;

        SeatViewDecorator(CommitteeSeatDto delegate) {
            this.delegate = delegate;
            this.order = delegate.order();
        }

        Long id() {
            return delegate.id();
        }

        String name() {
            return delegate.name();
        }

        Filling filling() {
            return delegate.filling();
        }

        GremisRole grantedRole() {
            return delegate.grantedRole();
        }
    }

    private class CommitteeMenuBar extends MenuBar {

        private final MenuItem workingGroupMenuItem;

        CommitteeMenuBar() {
            var createMenuItem = addItem(getTranslation("common.add"));
            var subMenu = createMenuItem.getSubMenu();
            var newTermMenuItem = subMenu.addItem("Funkční období");
            newTermMenuItem.addClickListener(e -> openTermDialog());
            var newSeatMenuItem = subMenu.addItem("Funkci");
            newSeatMenuItem.addClickListener(e -> openSeatDialog());
            workingGroupMenuItem = subMenu.addItem("Pracovní skupinu");
            workingGroupMenuItem.addClickListener(e -> {
                workingGroupDialog.setCommitteeId(committee.getId());
                workingGroupDialog.open();
            });
        }

        void setWorkingGroupEnabled(boolean enabled) {
            workingGroupMenuItem.setEnabled(enabled);
        }
    }
}
