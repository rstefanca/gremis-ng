package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeListener;
import cz.upce.gremis.gremisng.voting.domain.Resolution;

public class ResolutionForm extends VerticalLayout {


    private final TextField text = new TextField("Text usnesení");

    private final TextArea note = new TextArea("Poznámka");

    private final Binder<Resolution> binder = new Binder<>(Resolution.class);


    public ResolutionForm() {
        setWidthFull();
        setPadding(false);

        text.setWidth("100%");
        add(text);
        note.setWidth("100%");
        add(note);

        binder.forField(text)
                .asRequired()
                .bind(Resolution::getText, Resolution::setText);

        binder.forField(note)
                .bind(Resolution::getNote, Resolution::setNote);

    }

    void setResolution(Resolution resolution) {
        binder.readBean(resolution);
    }

    void setReadOnly(boolean readOnly) {
        binder.setReadOnly(readOnly);
    }

    boolean writeIfValid(Resolution resolution) {
        return binder.writeBeanIfValid(resolution);
    }

    public void addStatusChangeListener(StatusChangeListener listener) {
        binder.addStatusChangeListener(listener);
    }
}
