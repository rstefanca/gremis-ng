package cz.upce.gremis.gremisng;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Push
@PWA(name = "Gremis",
        shortName = "Gremis")
@Theme("gremis")
public class AppShell implements AppShellConfigurator {
}