package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.html.Span;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpanHelpers {

    public static void setupStateSpan(Span span, boolean isActive) {
        String suffix = isActive ? "active" : "disabled";
        String theme = String.format("badge %s",
                isActive ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(span.getTranslation("common.state." + suffix));
    }

}
