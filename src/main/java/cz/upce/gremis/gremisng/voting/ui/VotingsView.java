package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.ui.MainLayout;

@Route(value = "/votings", layout = MainLayout.class)
public class VotingsView extends VerticalLayout {

    public VotingsView() {
        add(new H1("Hlasovani"));
    }
}
