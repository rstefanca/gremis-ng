package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.upce.gremis.gremisng.assignments.domain.BoardSeatAssignment;
import cz.upce.gremis.gremisng.assignments.service.BoardSeatAssignmentService;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PersonRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AssignmentDialog extends Dialog {

    private final BoardSeatAssignmentService service;
    private final PersonRepository personRepository;

    private final Binder<BoardSeatAssignment> binder = new Binder<>(BoardSeatAssignment.class);

    private BoardSeat selectedSeat;

    private ComboBox<BoardSeat> seatCombo = new ComboBox<>("Funkce");
    private ComboBox<BoardTerm> termCombo = new ComboBox<>("Funkční období");

    private ComboBox<Person> personCombo = new ComboBox<>("Osoba");

    DatePicker from = new DatePicker("Od");
    DatePicker to = new DatePicker("Do");


    public AssignmentDialog(BoardSeatAssignmentService service, PersonRepository personRepository) {
        this.service = service;
        this.personRepository = personRepository;


        seatCombo.setItemLabelGenerator(BoardSeat::getName);
        termCombo.setItemLabelGenerator(BoardTerm::getDescription);
        personCombo.setItems(q -> personRepository.findAll(PageRequest.of(q.getPage(), q.getPageSize())).stream());
        personCombo.setItemLabelGenerator(Person::getFullName);

        setHeaderTitle("Přidat osobu");

        var dialogLayout = createDialogLayout();
        add(dialogLayout);

        var cancelButton = Buttons.createCancelButton(e -> {
            binder.removeBean();
            binder.refreshFields();
            this.close();
        });
        cancelButton.getStyle().set("margin-right", "auto");
        getFooter().add(cancelButton);

        Button saveButton = new Button(getTranslation("buttons.save"), (e) -> this.save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(saveButton);
    }

    private VerticalLayout createDialogLayout() {
        var formLayout = new FormLayout();


        var note = new TextField("Poznámka");

        binder.forField(seatCombo)
                .asRequired()
                .bindReadOnly(BoardSeatAssignment::getSeat);

        binder.forField(termCombo)
                .asRequired()
                .bind(BoardSeatAssignment::getTerm, BoardSeatAssignment::setTerm);

        binder.forField(personCombo)
                .asRequired()
                .bind(BoardSeatAssignment::getPerson, BoardSeatAssignment::setPerson);

        binder.forField(from)
                .asRequired()
                .bind(BoardSeatAssignment::getStart, BoardSeatAssignment::setStart);

        binder.forField(to)
                .asRequired()
                .bind(BoardSeatAssignment::getEnd, BoardSeatAssignment::setEnd);

        binder.forField(note)
                .bind(BoardSeatAssignment::getNote, BoardSeatAssignment::setNote);

        formLayout.add(seatCombo, termCombo);
        formLayout.add(personCombo, 2);
        formLayout.add(from, to);
        formLayout.add(note, 2);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        from.setValue(LocalDate.now());
        return new VerticalLayout(formLayout);
    }

    public void setSelectedTerm(BoardTerm boardTerm) {
        termCombo.setItems(List.of(boardTerm));
        termCombo.setValue(boardTerm);
        termCombo.setEnabled(false);
        if (to.getValue() == null) {
            to.setValue(boardTerm.getEnd());
        }
    }

    public void setSelectedSeat(BoardSeat seat) {
        this.selectedSeat = seat;
        this.seatCombo.setItems(List.of(seat));
        this.seatCombo.setValue(seat);
        this.seatCombo.setEnabled(false);
    }

    private void save() {
        var boardSeatAssignment = new BoardSeatAssignment();
        boardSeatAssignment.setSeat(selectedSeat);
        if (binder.writeBeanIfValid(boardSeatAssignment)) {
            service.save(boardSeatAssignment);
            binder.removeBean();
            binder.refreshFields();
            this.close();// launch event?
            Notification.show("Osoba ulozena");
        }
    }
}
