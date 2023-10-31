package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ButtonFactory {

    public static final String BUTTONS_REMOVE_KEY = "buttons.remove";
    public static final String BUTTONS_CANCEL_KEY = "buttons.cancel";

    public static Button createDeleteButton() {
        var button = new Button(getTranslation(BUTTONS_REMOVE_KEY));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        button.addClassName("gr-delete-button");
        return button;
    }

    public static Button createCancelButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        var button = new Button(getTranslation(BUTTONS_CANCEL_KEY), clickListener);
        //button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClassName("gr-cancel-button");
        return button;
    }

    public static Button createDeleteIconButton() {
        var removeButton = new Button();

        removeButton.addThemeVariants(
                ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);

        removeButton.setIcon(new Icon(VaadinIcon.TRASH));
        return removeButton;
    }

    public static Button createEditIconButton() {
        var removeButton = new Button();

        removeButton.addThemeVariants(
                ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_TERTIARY);

        removeButton.setIcon(new Icon(VaadinIcon.EDIT));
        return removeButton;
    }

    public static Button createSaveButton() {
        var button = new Button(getTranslation("buttons.save"));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    public static Button createSaveButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        var button = createSaveButton();
        button.addClickListener(clickListener);
        return button;
    }

    private static String getTranslation(String key) {
        return UI.getCurrent().getTranslation(key);
    }
}
