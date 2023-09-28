package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeListener;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;


class BoardTermForm extends FormLayout {

    private final Binder<BoardTerm> binder = new Binder<>(BoardTerm.class);

    private final TextField description;

    private final DatePicker from;
    private final DatePicker to;

    private boolean descriptionAlreadyExists;
    private Binder.Binding<BoardTerm, String> descriptionBinder;


    public BoardTermForm() {

        setWidthFull();

        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        description = new TextField("Popis");
        description.setClearButtonVisible(true);
        description.setValueChangeMode(ValueChangeMode.ON_BLUR);

        from = new DatePicker("Začátek");
        to = new DatePicker("Konec");

        add(description, 2);
        add(from, to);


        configureBinding();
    }

    private void configureBinding() {
        descriptionBinder = binder.forField(description)
                .asRequired()
                .withValidator((Validator<String>) (value, context) -> {
                    if (descriptionAlreadyExists) {
                        return ValidationResult.error("Název již existuje");
                    }
                    return ValidationResult.ok();
                })
                .bind(BoardTerm::getDescription, BoardTerm::setDescription);

        var fromBinder = binder.forField(from)
                .asRequired()
                .withValidator(date -> !isIntervalSet() || date.isBefore(to.getValue()),
                        "Neplatny začátek období"
                )
                .bind(BoardTerm::getStart, BoardTerm::setStart);

        var toBinder = binder.forField(to)
                .asRequired()
                .withValidator(toDate -> isIntervalSet() && toDate.isAfter(from.getValue()),
                        "Neplatný konec období"
                )
                .bind(BoardTerm::getEnd, BoardTerm::setEnd);

        from.addValueChangeListener(e -> {
            to.setInvalid(false);
            fromBinder.validate();
        });

        to.addValueChangeListener(e -> {
            from.setInvalid(false);
            toBinder.validate();
        });
    }

    void readBean(BoardTerm boardTerm) {
        binder.readBean(boardTerm);
    }

    boolean writeBeanIfValid(BoardTerm boardTerm) {
        return binder.writeBeanIfValid(boardTerm);
    }

    @Deprecated
    BoardTerm getBoardTerm() {
       return null;
    }

    @Deprecated
    void setBoardTerm(BoardTerm boardTerm) {
        binder.readBean(boardTerm);
    }

    private boolean isIntervalSet() {
        return from.getValue() != null && to.getValue() != null;
    }

    Registration addDescriptionValueChangeListener(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        return description.addValueChangeListener(listener);
    }

    void setDescriptionExists(boolean descriptionAlreadyExists) {
        this.descriptionAlreadyExists = descriptionAlreadyExists;
        descriptionBinder.validate();
    }

    boolean isValid() {
        return binder.isValid();
    }

    void addStatusChangeListener(StatusChangeListener listener) {
        binder.addStatusChangeListener(listener);
    }

    void clear() {
        binder.removeBean();
    }


}
