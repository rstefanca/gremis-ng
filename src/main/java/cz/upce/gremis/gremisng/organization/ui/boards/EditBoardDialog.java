package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.organization.domain.Board;

import lombok.Getter;


class EditBoardDialog extends Dialog {

    private final BoardForm boardForm;

    EditBoardDialog() {
        setHeaderTitle("Upravit");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        boardForm = new BoardForm();
        add(boardForm);

        addDialogCloseActionListener(e -> closeInternal());

        configureButtons();
    }

    void setBoard(Board board) {
        boardForm.setBoard(board);
    }

    public Registration addSaveListener(ComponentEventListener<EditBoardDialog.SaveEvent> listener) {
        return addListener(EditBoardDialog.SaveEvent.class, listener);
    }

    private void configureButtons() {
        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> validateAndSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        boardForm.addStatusChangeListener(e -> {
            var hasChanges = e.getBinder().hasChanges();
            var isValid = e.getBinder().isValid();
            saveButton.setEnabled(hasChanges && isValid);
        });

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> closeInternal());

        getFooter().add(cancelButton, saveButton);
    }

    void validateAndSave() {
        if (boardForm.writeBeanIfValid()) {
            fireEvent(new SaveEvent(this, boardForm.getBoard()));
        }
    }

    private void closeInternal() {
        close();
    }

    @Getter
    public static final class SaveEvent extends ComponentEvent<EditBoardDialog> {
        private final Board board;

        SaveEvent(EditBoardDialog source, Board board) {
            super(source, false);
            this.board = board;
        }
    }

}
