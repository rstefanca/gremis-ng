package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import cz.upce.gremis.gremisng.voting.domain.Resolution;

public class ResolutionForm extends Div {
    private final VerticalLayout content;

    private final TextField text = new TextField("Text usnesení");

    private final TextArea note = new TextArea("Poznámka");

    private final Binder<Resolution> binder = new Binder<>(Resolution.class);



    public ResolutionForm() {
        this.content = new VerticalLayout();
        content.setWidthFull();
        add(content);

        text.setWidth("100%");
        content.add(text);

        note.setWidth("100%");

        content.add(note);

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
}
