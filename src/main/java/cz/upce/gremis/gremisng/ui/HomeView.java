package cz.upce.gremis.gremisng.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.ui.MainLayout;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends Div {

    public HomeView() {
        add(new H1("Gremis home"));
    }

}
