package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

import static cz.upce.gremis.gremisng.ui.components.Buttons.BUTTONS_CANCEL_KEY;

public class ConfirmDeleteDialog extends ConfirmDialog {

    public ConfirmDeleteDialog(String text) {
        setRejectable(false);
        setCancelable(true);
        setHeader("Potrzení");
        setText(text);

        var deleteButton = Buttons.createDeleteButton();
        setConfirmButton(deleteButton);
        setCancelText(getTranslation(BUTTONS_CANCEL_KEY));
    }
}
