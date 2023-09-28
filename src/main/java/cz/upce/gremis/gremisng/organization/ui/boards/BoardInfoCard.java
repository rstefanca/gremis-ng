package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.organization.domain.Board;
import cz.upce.gremis.gremisng.organization.domain.BoardOption;

public class BoardInfoCard extends Div {

    //private final Grid<LabelAndValue> infoGrid;

    //private InfoCard<Board> infoCard;
    private final Span name = new Span();
    private final Span abbreviation = new Span();

    private final Span minMemberCount = new Span();

    private final Span termType = new Span();

    private final Span committees = new Span();

    private final Span perRollam = new Span();


    public BoardInfoCard() {

        addClassNames(
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_10);
        var verticalLayout = new VerticalLayout();
        verticalLayout.addClassNames(LumoUtility.BorderColor.CONTRAST_10);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);

        addRow(verticalLayout, "Název", name);
        addRow(verticalLayout, "Zkratka", abbreviation);
        addRow(verticalLayout, "Min. počet členů", minMemberCount);
        addRow(verticalLayout, "Typ funkčního období", termType);
        addRow(verticalLayout, "Komise", committees);
        addRow(verticalLayout, "Hlasováni per-rollam", perRollam);

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

    void setBoard(Board board) {
        name.setText(board.getName());
        abbreviation.setText(board.getAbbreviation());
        minMemberCount.setText(board.getMinMemberCount() + "");
        termType.setText(getTranslation("enum.termType." + board.getTermType()));
        committees.setText(renderOptionValue(board, BoardOption.COMMITTEES));
        perRollam.setText(renderOptionValue(board, BoardOption.PER_ROLLAM));
    }

    private static String renderOptionValue(Board board, BoardOption boardOption) {
        return board.getOptions().contains(boardOption) ? "Ano" : "Ne";
    }
}
