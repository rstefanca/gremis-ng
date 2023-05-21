package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;


class SeatDialog extends Dialog {

    private final SeatForm seatForm;

    public SeatDialog() {
        setHeaderTitle("Nová funkce");

        var error = new Span("Nelze pridat obdobi");
        error.setClassName(LumoUtility.Background.ERROR_10);
        error.getStyle().set("padding", "5px");


        seatForm = new SeatForm();

        add(error, seatForm);

        Button saveButton = new Button(getTranslation("buttons.save"), (e) -> {
            var seat = seatForm.getItem();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");
        getFooter().add(saveButton);

        getFooter().add(new Button(getTranslation("buttons.cancel"), e -> {
            seatForm.clear();
            this.close();
        }));
    }
}
