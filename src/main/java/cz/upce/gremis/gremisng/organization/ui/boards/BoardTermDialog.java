package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.organization.domain.BoardTerm;
import cz.upce.gremis.gremisng.ui.components.ButtonFactory;
import lombok.Getter;

// TODO use callback instead of the event?
class BoardTermDialog extends Dialog {

    private final BoardTermForm form;

    BoardTermDialog() {
        setCloseOnOutsideClick(false);
        setCloseOnEsc(true);
        form = new BoardTermForm();

        add(form);

        var saveButton = ButtonFactory.createSaveButton();

        saveButton.addClickListener(e -> fireEvent(new DialogSaveEvent(this, form)));

        getFooter().add(ButtonFactory.createCancelButton(e -> {
            form.clear();
            this.close();
        }));
        getFooter().add(saveButton);

        form.addStatusChangeListener(e -> {
            var hasChanges = e.getBinder().hasChanges();
            var isValid = e.getBinder().isValid();
            saveButton.setEnabled(hasChanges && isValid);
        });
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
