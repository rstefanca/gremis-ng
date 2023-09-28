package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.organization.domain.TermDto;

public class TermInfoCard extends Div {

    //private final Grid<LabelAndValue> infoGrid;

    //private InfoCard<Board> infoCard;
    private final Span description = new Span();
    private final Span start = new Span();

    private final Span end = new Span();




    public TermInfoCard() {

        addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_10);
        var verticalLayout = new VerticalLayout();
        verticalLayout.addClassNames(LumoUtility.BorderColor.CONTRAST_10);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);

        addRow(verticalLayout, "Název", description);
        addRow(verticalLayout, "Začátek", start);
        addRow(verticalLayout, "Konec", end);

        add(verticalLayout);
    }

    private void addRow(VerticalLayout layout, String label, Component valueComponent) {
        var rowLayout = new HorizontalLayout();
        var labelSpan = new Span(label);
        labelSpan.setWidth("16em");
        labelSpan.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.SMALL,
                LumoUtility.FontWeight.MEDIUM);
        rowLayout.add(labelSpan);
        rowLayout.addAndExpand(valueComponent);
        valueComponent.addClassNames(
                LumoUtility.Padding.SMALL
        );
        layout.add(rowLayout);
    }

    void setTerm(TermDto term) {
        description.setText(term.description());
        start.setText(term.start().toString());
        end.setText(term.end().toString());
    }

}
