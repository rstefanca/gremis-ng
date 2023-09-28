package cz.upce.gremis.gremisng.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import cz.upce.gremis.gremisng.organization.ui.boards.NewBoardView;
import cz.upce.gremis.gremisng.organization.ui.components.InfoLayout;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.voting.ui.NewVotingView;
import cz.upce.gremis.gremisng.voting.ui.VotesView;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSizeFull();
        setSpacing(true);


        add(new H1("Dashobard"));

        var boards = new InfoLayout("Gremia");
        boards.add(new Div(new Span("WIP")));
        boards.setWidthFull();
        add(boards);
        var runnigVotes = new InfoLayout("Probíhající hlasování");
        runnigVotes.add(new Div(new Span("WIP")));
        runnigVotes.setWidthFull();
        add(runnigVotes);




    }

}
