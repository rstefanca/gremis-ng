package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.organization.domain.Board;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import cz.upce.gremis.gremisng.organization.domain.TermDto;
import cz.upce.gremis.gremisng.organization.ui.components.InfoLayout;
import cz.upce.gremis.gremisng.ui.AsyncDataCallback;
import cz.upce.gremis.gremisng.ui.components.ConfirmDeleteDialog;
import cz.upce.gremis.gremisng.ui.components.EmptyPanel;
import cz.upce.gremis.gremisng.ui.components.NotificationHelper;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

class BoardDetail extends VerticalLayout {


    private final BoardViewLogic boardViewLogic;

    private final BoardInfoCard infoCard;

    private final TermInfoCard termInfoCard;

    private final TermMenu termMenuWrapper;

    private final Details assigneesDetail;

    private final Details votingDetails;

    private final Details meetingDetails;

    private final EmptyPanel termEmptyPanel;

    private Long termId;
    private Board board;

    BoardDetail(BoardViewLogic boardViewLogic, BoardViewData viewData) {
        this.boardViewLogic = boardViewLogic;

        infoCard = new BoardInfoCard();
        infoCard.setWidthFull();

        add(new H3("Grémium"));
        add(infoCard);


        termInfoCard = new TermInfoCard();
        termInfoCard.setWidthFull();
        termInfoCard.setVisible(false);
        termMenuWrapper = new TermMenu();

        //viewLogic.openTermDialog()
        termMenuWrapper.setEditClickListener(e -> viewData.getTerm(termId, new AsyncDataCallback<>() {
            @Override
            public void operationFinished(BoardTerm data) {
                accessUi(() -> openTermDialog(data, viewData));
            }

            @Override
            public void operationFailed(Throwable error) {
                accessUi(() -> NotificationHelper.showError(error));
            }
        }));

        termEmptyPanel = new EmptyPanel("Nic tu není", "Vytvořit");
        termEmptyPanel.setVisible(false);
        termEmptyPanel.addActionClickListener(e -> openTermDialog(new BoardTerm(), viewData));

        var termInfo = configureTermLayout();
        add(termInfo);

        // details
        assigneesDetail = new Details("Osoby ve funkcích", new LazyAssigneeDetails());
        votingDetails = new Details("Hlasování", new Span("Hlasování"));
        meetingDetails = new Details("Zasedání", new Span("Zasedání"));

        add(assigneesDetail, votingDetails, meetingDetails);

    }

    //TODO move it somewhere where its more accessible to other parts of the page
    void openTermDialog(BoardTerm term, BoardViewData viewData) {
        var title = term.isNew() ? "Vytvořit funkční obodbí" : "Upravit funkční období";
        var dialog = new BoardTermDialog();
        dialog.readTerm(term);
        dialog.setHeaderTitle(title);
        dialog.getFooter().add();
        dialog.addSaveListener(e -> {
            if (e.getForm().writeBeanIfValid(term)) {
                if (term.isNew()) {
                    term.setBoard(board);
                }
                viewData.saveTerm(term)
                        .thenAccept(saved -> accessUi(() -> {
                            dialog.close();
                            NotificationHelper.showSuccess("Funkční období uloženo");
                            var termDto = new TermDto(saved.getId(),
                                    saved.getDescription(),
                                    saved.getStart(),
                                    saved.getEnd());
                            setTerm(termDto);
                            fireEvent(new TermChangedEvent(this, termDto));
                        }))
                        .exceptionally(err -> {
                            accessUi(() -> NotificationHelper.showError(err));
                            return null;
                        });
            }
        });

        dialog.addDescriptionValueChangeListener(e -> {
            var description = e.getValue();
            viewData.getTerms(board.getId(), new AsyncDataCallback<>() {
                @Override
                public void operationFinished(List<TermDto> data) {
                    boolean descriptionExists = data.stream()
                            .filter(t -> !t.id().equals(termId))
                            .anyMatch(t -> t.description().equals(description));
                    accessUi(() -> dialog.setDescriptionExists(descriptionExists));
                }

                @Override
                public void operationFailed(Throwable error) {
                    // TODO log?
                }
            });
        });

        dialog.open();
    }

    private InfoLayout configureTermLayout() {
        var termInfo = new InfoLayout("Funkční odbobí");

        termInfo.add(termEmptyPanel);
        termInfo.add(termInfoCard);
        termInfo.addTitleSuffix(termMenuWrapper.getComponent());

        return termInfo;
    }

    void setBoard(Board board) {
        this.board = board;
        infoCard.setBoard(board);
    }

    void setTerm(TermDto term) {
        termId = term.id();
        setTermVisible(true);
        termInfoCard.setTerm(term);
    }

    void setVotingVisible(boolean visible) {
        votingDetails.setVisible(visible);
    }

    void setTermVisible(boolean visible) {
        termInfoCard.setVisible(visible);
        termMenuWrapper.setVisible(visible);
        termEmptyPanel.setVisible(!visible);
    }

    Registration addSaveListener(ComponentEventListener<TermChangedEvent> listener) {
        return addListener(TermChangedEvent.class, listener);
    }

    private void accessUi(Command command) {
        getUI().ifPresent(ui -> ui.access(command));
    }

    private class LazyAssigneeDetails extends Div {

        public LazyAssigneeDetails() {
            addAttachListener(e -> init());
        }

        private void init() {
            add(new Span("Osoby"));
        }
    }

    private static class TermMenu implements Serializable {

        private final MenuBar menuBar;
        private final MenuItem editItem;

        TermMenu() {
            menuBar = createMenuBar();

            var downItem = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
            editItem = downItem.getSubMenu().addItem("Upravit");


            var deleteItem = downItem.getSubMenu().addItem("Odstranit");
            deleteItem.addClickListener(e -> {
                var dialog = new ConfirmDeleteDialog("Opravdu chcete odstranit funkční období?");
                dialog.open();
                dialog.addConfirmListener(confirm -> {
                    //TODO delete action
                    Notification.show("Work in progress");
                });

            });
        }

        void setEditClickListener(ComponentEventListener<ClickEvent<MenuItem>> editClickListener) {
            editItem.addClickListener(editClickListener);
        }

        private static MenuBar createMenuBar() {
            var termMenu = new MenuBar();
            termMenu.addThemeVariants(
                    MenuBarVariant.LUMO_ICON,
                    MenuBarVariant.LUMO_TERTIARY);
            return termMenu;
        }

        MenuBar getComponent() {
            return menuBar;
        }

        void setVisible(boolean visible) {
            menuBar.setVisible(visible);
        }
    }

    @Getter
    public static class TermChangedEvent extends ComponentEvent<BoardDetail> {

        private final TermDto value;

        TermChangedEvent(BoardDetail source, TermDto value) {
            super(source, false);
            this.value = value;
        }
    }
}
