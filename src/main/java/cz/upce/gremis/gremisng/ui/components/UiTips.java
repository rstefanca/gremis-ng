package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UiTips {

    public Div addTip(HasComponents parent, String text) {
        var icon = new Icon(VaadinIcon.LIGHTBULB);
        icon.addClassNames(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.TERTIARY);
        var textSpan = new Span(text);
        textSpan.addClassNames(LumoUtility.Margin.Left.SMALL, LumoUtility.TextColor.TERTIARY);
        var div = new Div(
                icon,
                textSpan);
        parent.add(div);
        return div;
    }
}
