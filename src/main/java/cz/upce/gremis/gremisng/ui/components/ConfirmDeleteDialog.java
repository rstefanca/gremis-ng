package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

import static cz.upce.gremis.gremisng.ui.components.ButtonFactory.BUTTONS_CANCEL_KEY;

public class ConfirmDeleteDialog extends ConfirmDialog {

    public ConfirmDeleteDialog(String text) {
        setRejectable(false);
        setCancelable(true);
        setHeader("Potrzení");
        setText(text);

        var deleteButton = ButtonFactory.createDeleteButton();
        setConfirmButton(deleteButton);
        setCancelText(getTranslation(BUTTONS_CANCEL_KEY));
    }
}
