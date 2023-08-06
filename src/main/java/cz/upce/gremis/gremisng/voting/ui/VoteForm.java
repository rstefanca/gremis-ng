package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import cz.upce.gremis.gremisng.voting.domain.Vote;

public class VoteForm extends Div {

    private final VerticalLayout content;

    private final TextField name = new TextField("Název");

    private final DateTimePicker startAt = new DateTimePicker("Začátek");

    private final DateTimePicker end = new DateTimePicker("Konec");

    private final TextArea coverLetter = new TextArea("Průvodní slovo");

    private final TextArea note = new TextArea("Poznámka");

    private final Binder<Vote> binder = new Binder<>(Vote.class);


    public VoteForm() {
        this.content = new VerticalLayout();
        content.setSizeUndefined();
        add(content);

        var formLayout = new FormLayout();
        content.add(formLayout);

        formLayout.add(name, 2);
        formLayout.add(startAt, end);
        formLayout.add(coverLetter, 2);
        formLayout.add(note, 2);

        binder.forField(name)
                .bind(Vote::getName, Vote::setName);

        binder.forField(startAt)
                .bindReadOnly(Vote::getStartTime);

        binder.forField(end)
                .asRequired()
                .bind(Vote::getEndTime, Vote::setEndTime);

        binder.forField(coverLetter)
                .asRequired()
                .bind(Vote::getCoverLetter, Vote::setCoverLetter);

        binder.forField(note)
                .bind(Vote::getNote, Vote::setNote);
    }

    void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    void setVote(Vote vote) {
        binder.readBean(vote);

    }

    boolean writeBeanIfValid(Vote vote) {
        return binder.writeBeanIfValid(vote);
    }

}
