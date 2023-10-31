package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.common.PerRollamApproveType;
import cz.upce.gremis.gremisng.organization.domain.Board;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import cz.upce.gremis.gremisng.organization.service.BoardAggregateService;
import cz.upce.gremis.gremisng.time.BusinessTime;
import cz.upce.gremis.gremisng.ui.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Route(value = "/new-board", layout = MainLayout.class)
@RolesAllowed("ROOT")
public class NewBoardView extends VerticalLayout {

    private final BoardAggregateService service;


    private final Button saveButton;

    private final Button resetButton;


    private final BoardForm boardForm;

    private final BoardTermForm boardTermForm;

    private final SeatsPanel seats;

    NewBoardView(BoardAggregateService service, BusinessTime businessTime) {
        this.service = service;

        setWidthFull();

        var title = new H2("Definice nového grémia");

        boardForm = new BoardForm();
        boardTermForm = new BoardTermForm();
        seats = new SeatsPanel();

        var accordion = new Accordion();

        accordion.setWidth("75%");
        var boardDetailsPanel = accordion.add("Definice grémia", boardForm);
        var termPanel = accordion.add("Funkční období", boardTermForm);
        termPanel.setEnabled(false);
        var seatsPanel = accordion.add("Funkce", seats);
        seatsPanel.setEnabled(false);

        /*
        var summaryPanel = accordion.add("Shrnutí TODO", new Div());
        summaryPanel.setEnabled(false);
         */


        var continueToTerm = new Button(getTranslation("buttons.continue"),
                e -> {
                    if (boardForm.isValid()) {
                        termPanel.setOpened(true);
                    } else {
                        boardForm.showErrors();
                    }
                });

        var continueToSeats = new Button(getTranslation("buttons.continue"),
                e -> {
                    if (boardTermForm.isValid()) {
                        seatsPanel.setOpened(true);
                    } else {
                        //boardForm.showErrors();
                    }
                });

        continueToTerm.addThemeVariants(ButtonVariant.LUMO_SMALL);
        continueToTerm.setEnabled(false);
        continueToSeats.addThemeVariants(ButtonVariant.LUMO_SMALL);

        boardDetailsPanel.addContent(continueToTerm);
        termPanel.addContent(continueToSeats);

        saveButton = new Button(getTranslation("buttons.save"), e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        resetButton = new Button("Vymazat", e -> {
            boardForm.reset();
            //boardTermForm.reset();
            accordion.close();
            accordion.open(boardDetailsPanel);
            seats.resetPanels();
        });

        boardForm.addStatusChangeListener(e -> {
            var hasChanges = e.getBinder().hasChanges();
            resetButton.setEnabled(hasChanges);

            var isValid = hasChanges && e.getBinder().isValid();
            continueToTerm.setEnabled(isValid);
            termPanel.setEnabled(isValid);
            seatsPanel.setEnabled(isValid);
            //summaryPanel.setEnabled(isValid);
            saveButton.setEnabled(isValid);
        });

        boardTermForm.addStatusChangeListener(e -> {
            var hasChanges = e.getBinder().hasChanges();
            var isValid = e.getBinder().isValid();
            continueToSeats.setEnabled(isValid && hasChanges);
            //summaryPanel.setEnabled(true);
            saveButton.setEnabled(isValid && hasChanges);
        });

        var buttonLayout = new HorizontalLayout(saveButton, resetButton);

        add(title, accordion, buttonLayout);

        boardForm.setBoard(new Board().setMinMemberCount(1));
        boardTermForm.setBoardTerm(new BoardTerm().setStart(businessTime.getDate()));
    }

    private void save() {
        log.debug("UI: Saving new board");
        var board = new Board();
        if (boardForm.writeBeanIfValid(board)) {
            board.setPerRollamApproveType(PerRollamApproveType.NONE);
            var term = boardTermForm.getBoardTerm();
            if (term != null) {
                board.add(term);
            }
            int order = 1;
            for (BoardSeat item : seats.getItems()) {
                item.setOrder(order++);
                board.add(item);
            }
            service.save(board);
            Notification.show("Saved");
            log.info("UI: New board saved id = {}", board.getId());
            getUI().ifPresent(ui -> ui.navigate(BoardView.class, board.getId()));
        }
    }
}
