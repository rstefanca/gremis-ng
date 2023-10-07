package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import lombok.Getter;

import java.util.function.Consumer;


class BoardTermDialog extends Dialog {

    private final BoardTermForm form;

    BoardTermDialog() {
        setCloseOnOutsideClick(false);
        setCloseOnEsc(true);
        form = new BoardTermForm();

        add(form);

        var saveButton = new Button(getTranslation("buttons.save"));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(e -> fireEvent(new DialogSaveEvent(this, form)));

        getFooter().add(new Button(getTranslation("buttons.cancel"), e -> {
            form.clear();
            this.close();
        }));
        getFooter().add(saveButton);
    }

    void readTerm(BoardTerm boardTerm) {
        form.readBean(boardTerm);
    }

    Registration addSaveListener(ComponentEventListener<DialogSaveEvent> listener) {
        return addListener(DialogSaveEvent.class, listener);
    }

    Registration addDescriptionValueChangeListener(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
        return form.addDescriptionValueChangeListener(listener);
    }

    void setDescriptionExists(boolean descriptionAlreadyExists) {
        form.setDescriptionExists(descriptionAlreadyExists);
    }

    @Getter
    public static class DialogSaveEvent extends ComponentEvent<BoardTermDialog> {

        private final BoardTermForm form;

        protected DialogSaveEvent(BoardTermDialog source, BoardTermForm form) {
            super(source, false);
            this.form = form;
        }
    }
}
