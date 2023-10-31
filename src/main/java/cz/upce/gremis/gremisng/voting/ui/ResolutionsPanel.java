package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableSupplier;

class ResolutionsPanel extends Div {

    private final VerticalLayout formsContainer = new VerticalLayout();

    public ResolutionsPanel(SerializableSupplier<Component> componentSupplier) {
        setWidthFull();
        formsContainer.setPadding(false);
        formsContainer.setWidthFull();
        add(formsContainer);

        formsContainer.add(new FormPanel(componentSupplier));
    }

    void resetPanels() {
        var extra = formsContainer.getChildren()
                .skip(1)
                .toList();
        formsContainer.remove(extra);
        extra.forEach(formsContainer::remove);
    }

    private void removePanel(FormPanel formPanel) {
        formsContainer.remove(formPanel);
        updateButtons();
    }

    private void movePanel(FormPanel panel, int increment) {
        var currentIndex = formsContainer.indexOf(panel);
        formsContainer.remove(panel);
        formsContainer.addComponentAtIndex(currentIndex + increment, panel);
        panel.up.setEnabled(true);
        panel.down.setEnabled(true);
        updateButtons();
    }

    private void updateButtons() {
        var total = formsContainer.getComponentCount();
        if (total == 1) {
            getFormPanelAt(0)
                    .setButtonsState(false, false, false);
            return;
        }

        var lastIndex = total - 1;
        for (int i = 0; i < total; i++) {
            var current = getFormPanelAt(i);
            if (i == 0) {
                current.setButtonsState(true, false, true);
            } else if (i == lastIndex) {
                current.setButtonsState(true, true, false);
            } else {
                current.setButtonsState(true, true, true);
            }
        }
    }

    private FormPanel getFormPanelAt(int index) {
        return (FormPanel) formsContainer.getComponentAt(index);
    }

    public class FormPanel extends VerticalLayout {

        private final Component content;

        private final Button up = new Button(new Icon(VaadinIcon.ARROW_UP), e -> movePanel(this, -1));
        private final Button down = new Button(new Icon(VaadinIcon.ARROW_DOWN), e -> movePanel(this, 1));
        private final Button remove = new Button(new Icon(VaadinIcon.MINUS), e -> removePanel(this));

        FormPanel(SerializableSupplier<Component> componentSupplier) {
            setPadding(false);
            setWidthFull();
            this.content = componentSupplier.get();

            var add = new Button(new Icon(VaadinIcon.PLUS), event -> {
                var newPanel = new FormPanel(componentSupplier);
                newPanel.remove.setEnabled(true);
                var index = formsContainer.indexOf(this);
                var indexAfter = index + 1;
                var componentCount = formsContainer.getComponentCount();
                if (indexAfter == componentCount) {
                    formsContainer.add(newPanel);
                    newPanel.setLast();
                } else {
                    newPanel.up.setEnabled(true);
                    newPanel.down.setEnabled(true);
                    formsContainer.addComponentAtIndex(indexAfter, newPanel);
                }
                down.setEnabled(true);
                remove.setEnabled(true);
            });


            var buttons = new Div(add, remove, up, down);

            add(content, buttons);
        }

        void setLast() {
            up.setEnabled(true);
            down.setEnabled(false);
        }

        void setButtonsState(boolean removeEnabled, boolean upEnabled, boolean downEnabled) {
            remove.setEnabled(removeEnabled);
            up.setEnabled(upEnabled);
            down.setEnabled(downEnabled);
        }
    }

    /*
    public static sealed class FormPanelEvent extends ComponentEvent<FormPanel> {

        protected FormPanelEvent(FormPanel formPanel) {
            super(formPanel, false);
        }

    }
    */

}
