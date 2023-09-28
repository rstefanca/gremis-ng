package cz.upce.gremis.gremisng.organization.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InfoLayout extends VerticalLayout implements HasComponents {

    private final HorizontalLayout titleArea;
    private Component suffixComponent;

    public InfoLayout(String title) {
        setPadding(false);
        setSpacing(true);
        titleArea = new HorizontalLayout();
        titleArea.setWidthFull();
        titleArea.setAlignItems(Alignment.CENTER);
        titleArea.addAndExpand(new H3(title));
        add(titleArea);
    }

    public void addTitleSuffix(Component component) {
        titleArea.add(component);
        this.suffixComponent = component;
    }

    public void showSuffixComponent(boolean visible) {
        suffixComponent.setVisible(visible);
    }

}
