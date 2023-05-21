package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.common.PerRollamApproveType;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.service.BoardAggregateService;
import cz.upce.gremis.gremisng.ui.MainLayout;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Route(value = "/new-board", layout = MainLayout.class)
public class NewBoardView extends VerticalLayout {

    private final BoardAggregateService service;

    private BoardForm boardForm;
    private BoardTermForm boardTermForm;

    private SeatsPanel seats = new SeatsPanel();

    public NewBoardView(BoardAggregateService service) {
        this.service = service;
        setWidthFull();
        var title = new H2("Definice noveho gremia");

        var accordion = new Accordion();

        boardForm = new BoardForm();

        boardTermForm = new BoardTermForm();

        var boardDetailsPanel = accordion.add("Definice gremia", boardForm);
        var termPanel = accordion.add("Funkcni obdobi", boardTermForm);
        var seatsPanel = accordion.add("Funkce", seats);

        Button continueToTerm = new Button(getTranslation("buttons.continue"),
                (e) -> {
                    if (boardForm.isValid()) {
                        termPanel.setOpened(true);
                    } else {
                        boardForm.showErrors();
                    }
                });

        Button continueToSeats = new Button(getTranslation("buttons.continue"),
                (e) -> {
                    if (boardTermForm.isValid()) {
                        seatsPanel.setOpened(true);
                    } else {
                        //boardForm.showErrors();
                    }
                });


        continueToTerm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        continueToTerm.setEnabled(false);

        boardDetailsPanel.addContent(continueToTerm);
        termPanel.addContent(continueToSeats);



        Button saveButton = new Button(getTranslation("buttons.save"), (e) -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        boardForm.addStatusChangeListener(e -> {
            continueToTerm.setEnabled(!e.hasValidationErrors());
            saveButton.setEnabled(!e.hasValidationErrors());
        });

        add(title, accordion, saveButton);
    }

    private void save() {
        log.debug("UI: Saving new board");
        var board = boardForm.getBoard();
        board.setPerRollamApproveType(PerRollamApproveType.NONE);
        var term = boardTermForm.getItem();
        board.add(term);
        int order = 1;
        for (BoardSeat item : seats.getItems()) {
            item.setOrder(order++);
            board.add(item);
        }
        service.save(board);
        Notification.show("Saved");
        log.info("UI: New board saved id = {}", board.getId());
        getUI().ifPresent(ui -> ui.navigate(BoardDetailView.class, board.getId()));
    }

    private FormLayout createFormLaoyout() {
        var formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));
        return formLayout;
    }
}
