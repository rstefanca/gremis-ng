package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;

import java.time.LocalDate;

// TODO use events, add buttons here (remove from dialog)
public class BoardTermForm extends FormLayout {

    private Binder<BoardTerm> binder = new Binder<>(BoardTerm.class);

    private DatePicker from = new DatePicker("Začátek");
    private DatePicker to = new DatePicker("Konec");

    private BoardTerm item;

    public BoardTermForm() {

        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        var description = new TextField("Popis");

        from.setValue(LocalDate.now());

        binder.forField(description)
                .asRequired()
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

        add(description, 2);
        add(from, to);
    }

    BoardTerm getItem() {
        binder.writeBeanIfValid(item);
        return item;
    }

    void setItem(BoardTerm item) {
        this.item = item;
        binder.readBean(item);
    }

    private boolean isIntervalSet() {
        return from.getValue() != null && to.getValue() != null;
    }

    boolean isValid() {
        return binder.isValid();
    }

    void clear() {
        binder.removeBean();
        binder.refreshFields();
    }
}
