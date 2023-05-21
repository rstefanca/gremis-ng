package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class Buttons {

    public static final String BUTTONS_REMOVE_KEY = "buttons.remove";
    public static final String BUTTONS_CANCEL_KEY = "buttons.cancel";

    public static Button createDeleteButton() {
        var button = new Button(getTranslation(BUTTONS_REMOVE_KEY));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        button.addClassName("gr-delete-button");
        return button;
    }

    public static Button createCancelButton() {
        return createCancelButton(null);
    }

    public static Button createCancelButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        var button = new Button(getTranslation(BUTTONS_CANCEL_KEY), clickListener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("gr-cancel-button");
        return button;
    }

    private static String getTranslation(String key) {
        return UI.getCurrent().getTranslation(key);
    }
}
