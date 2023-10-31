package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableConsumer;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.ui.utils.BinderUtils;
import cz.upce.gremis.gremisng.ui.components.ButtonFactory;

class SeatDialog extends Dialog {

    public SeatDialog(BoardSeat seat, SerializableConsumer<Dialog> saveAction) {
        setHeaderTitle(seat.isNew() ? "Nová funkce" : "Upravit funkci");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        var seatForm = new SeatForm();
        seatForm.setItem(seat);
        add(seatForm);

        var saveButton = ButtonFactory.createSaveButton(e -> {
            if (seatForm.writeBeanIfValid(seat)) {
               saveAction.accept(this);
           }
        });
        saveButton.setEnabled(false);

        getFooter().add(ButtonFactory.createCancelButton(e -> this.close()));
        getFooter().add(saveButton);

        seatForm.addStatusChangeListener(BinderUtils.createEnableButtonStatusChangeListener(saveButton));
    }
}
