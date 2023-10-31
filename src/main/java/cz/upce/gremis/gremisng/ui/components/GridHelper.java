package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GridHelper {

    public static <T> Grid.Column<T> createGridOrderColumn(Grid<T> grid, SerializableFunction<T, Integer> orderFunction) {
        return grid.addColumn(new ComponentRenderer<>(source -> AvatarFactory.createOrderAvatar(orderFunction.apply(source))))
                .setHeader("Pořadí")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("5em")
                .setFlexGrow(0)
                .setResizable(false);
    }
}
