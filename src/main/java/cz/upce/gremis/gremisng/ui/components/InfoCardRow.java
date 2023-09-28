package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.function.ValueProvider;

@Tag("info-card-row")
@JsModule("components/info-card/info-card-row.js")
public class InfoCardRow<T> extends LitTemplate {

    private static final String LABEL_PROPERTY = "label";
    private final ValueProvider<T, ?> valueProvider;

    public InfoCardRow(ValueProvider<T, ?> valueProvider) {
        this.valueProvider = valueProvider;

    }

    public InfoCardRow setLabel(String label) {
        getElement().setProperty(LABEL_PROPERTY, label);
        return this;
    }

    void applyValue(T item) {
        Object value = valueProvider.apply(item);
        String stringValue = value != null ? value.toString() : "";
        Element slotElement = ElementFactory.createSpan(stringValue);
        getElement().appendChild(slotElement);
    }
}
