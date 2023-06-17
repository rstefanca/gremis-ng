package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class EmptyListPanel extends HorizontalLayout {

    private final Button actionButton;


    public EmptyListPanel(String text, String buttonText) {
        setWidthFull();
        setPadding(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(new Text(text));

        actionButton = new Button(buttonText);
        add(actionButton);
    }

    public void addActionClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        actionButton.addClickListener(listener);
    }
}
