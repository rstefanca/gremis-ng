package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.gremis.gremisng.assignments.domain.CommitteeSeatAssignment;
import cz.upce.gremis.gremisng.assignments.service.CommitteeSeatAssignmentService;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PersonRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

// TODO - nahradit  termComboBox komponentou

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class CommitteeAssignmentDialog extends Dialog {

    private final Binder<CommitteeSeatAssignment> binder = new Binder<>(CommitteeSeatAssignment.class);

    private final ComboBox<Person> personCombo = new ComboBox<>("Osoba");

    private final CommitteeTermsComboBox termComboBox;

    private final ComboBox<CommitteeSeatDto> seatComboBox = new ComboBox<>("Funkce");

    private final DatePicker from = new DatePicker("Od");

    private final DatePicker to = new DatePicker("Do");

    private final TextField note = new TextField("Poznámka");

    private final CommitteeSeatAssignmentService service;

    private final CommitteeTermRepository committeeTermRepository;

    private final CommitteeSeatRepository committeeSeatRepository;


    private Long committeeId;

    CommitteeAssignmentDialog(PersonRepository personRepository,
                              CommitteeSeatAssignmentService service,
                              CommitteeTermRepository committeeTermRepository,
                              CommitteeSeatRepository committeeSeatRepository,
                              CommitteeTermsComboBox committeeTermsComboBox) {
        this.service = service;
        this.committeeTermRepository = committeeTermRepository;
        this.committeeSeatRepository = committeeSeatRepository;
        this.termComboBox = committeeTermsComboBox;

        committeeTermsComboBox.setCommitteeId(committeeId);

        configureForm();
        configureButtons();
        configureBinder();

        termComboBox.setItemLabelGenerator(TermDto::description);
        seatComboBox.setItemLabelGenerator(CommitteeSeatDto::name);
        personCombo.setItemLabelGenerator(Person::getFullName);

        personCombo.setItems(q -> personRepository.findAll(PageRequest.of(q.getPage(), q.getPageSize())).stream());

    }

    private void configureForm() {
        var layout = new FormLayout();
        layout.setWidthFull();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        layout.add(seatComboBox, termComboBox);
        layout.add(personCombo, 2);
        layout.add(from, to);
        layout.add(note, 2);

        from.setValue(LocalDate.now());

        add(layout);

        seatComboBox.setItems((CallbackDataProvider.FetchCallback<CommitteeSeatDto, String>)query -> {
            VaadinSpringDataHelpers.toSpringPageRequest(query); //cheat
            return committeeSeatRepository.getSeats(committeeId).stream();
        });

    }

    private void configureBinder() {
        binder.forField(termComboBox)
                        .asRequired()
                                .withConverter(new Converter<TermDto, CommitteeTerm>() {
                                    @Override
                                    public Result<CommitteeTerm> convertToModel(TermDto value, ValueContext context) {
                                        var committeeTerm = committeeTermRepository.findById(value.id()).orElseThrow();
                                        return Result.ok(committeeTerm);
                                    }

                                    @Override
                                    public TermDto convertToPresentation(CommitteeTerm value, ValueContext context) {
                                        return new TermDto(value.getId(), value.getName(), value.getStart(), value.getEnd());
                                    }
                                })
                .bind(CommitteeSeatAssignment::getTerm, CommitteeSeatAssignment::setTerm);

        binder.forField(seatComboBox)
                        .asRequired()
                                .withConverter(new Converter<CommitteeSeatDto, CommitteeSeat>() {
                                    @Override
                                    public Result<CommitteeSeat> convertToModel(CommitteeSeatDto value, ValueContext context) {
                                        var seat = committeeSeatRepository.findById(value.id()).orElseThrow();
                                        return Result.ok(seat);
                                    }

                                    @Override
                                    public CommitteeSeatDto convertToPresentation(CommitteeSeat value, ValueContext context) {
                                        return null;
                                    }
                                })
                .bind(CommitteeSeatAssignment::getSeat, CommitteeSeatAssignment::setSeat);

        binder.forField(personCombo)
                .asRequired()
                .bind(CommitteeSeatAssignment::getPerson, CommitteeSeatAssignment::setPerson);

        binder.forField(from)
                .asRequired()
                .bind(CommitteeSeatAssignment::getStart, CommitteeSeatAssignment::setStart);

        binder.forField(to)
                .asRequired()
                .bind(CommitteeSeatAssignment::getEnd, CommitteeSeatAssignment::setEnd);

        binder.forField(note)
                .bind(CommitteeSeatAssignment::getNote, CommitteeSeatAssignment::setNote);
    }

    private void configureButtons() {
        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> {
                    reset();
                    close();
                });

        getFooter().add(saveButton, cancelButton);
    }

    private void save() {
        var bean = new CommitteeSeatAssignment();
        if (binder.writeBeanIfValid(bean)) {
            service.save(bean);
            Notification.show("Osoba ulozena");
            reset();
            close();
        }
    }

    private void reset() {
        binder.removeBean();
        binder.refreshFields();
    }

    void setSelectedSeat(CommitteeSeatDto selectedSeat) {
        seatComboBox.setValue(selectedSeat);
    }

    public void setCommitteeId(Long committeeId) {
        this.committeeId = committeeId;
        termComboBox.setCommitteeId(committeeId);
    }

    public void selectedTermId(Long termId) {
        termComboBox.setSelectedTermId(termId);
    }
}
