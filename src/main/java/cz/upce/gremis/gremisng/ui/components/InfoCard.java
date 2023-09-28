package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.ValueProvider;

public class InfoCard<T> extends Div {


    @SuppressWarnings("unchecked")
    public void setItem(T item) {
        getChildren().forEach(child -> {
            if (child instanceof InfoCardRow row) {
                row.applyValue(item);
            }
        });
    }

    public InfoCardRow addRow(ValueProvider<T, ?> valueProvider) {
        var infoCardRow = new InfoCardRow<>(valueProvider);
        add(infoCardRow);
        return infoCardRow;
    }


}
