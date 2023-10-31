package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class EmptyPanel extends HorizontalLayout {

    private final Button actionButton;

    public EmptyPanel(String buttonText) {
        this("Nic tu není", buttonText);
    }

    public EmptyPanel(String text, String buttonText) {
        setWidthFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        add(new Text(text));

        actionButton = new Button(buttonText);
        add(actionButton);
    }

    public void addActionClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        actionButton.addClickListener(listener);
    }
}
