package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.upce.gremis.gremisng.voting.domain.BallotDto;

import java.util.Collection;


public class BallotsPreviewDialog extends Dialog {

    private final Grid<BallotDto> grid;

    public BallotsPreviewDialog() {
        setHeaderTitle("Hlasovací lístky");
        setResizable(true);
        setWidth("50%");

        var content = new VerticalLayout();
        content.setHeightFull();

        grid = new Grid<>();
        grid.setWidthFull();
        grid.addColumn(BallotDto::voterFullName).setHeader("Jméno");
        grid.addColumn(BallotDto::ipAddress).setHeader("IP adresa");
        grid.addColumn(BallotDto::timestamp).setHeader("Čas");
        content.add(grid);
        content.expand(grid);

        add(content);
        var closeButton = new Button(getTranslation("buttons.close"), e -> close());
        getFooter().add(closeButton);
    }

    public void setBallots(Collection<BallotDto> ballots) {
        grid.setItems(ballots);
    }
}
