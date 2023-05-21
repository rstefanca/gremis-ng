package cz.upce.gremis.gremisng;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Push
//@Theme(variant = Lumo.DARK)
@PWA(name = "Gremis",
        shortName = "Gremis")
public class AppShell implements AppShellConfigurator {
}