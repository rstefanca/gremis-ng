package cz.upce.gremis.gremisng.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.upce.gremis.gremisng.organization.ui.boards.BoardsView;
import cz.upce.gremis.gremisng.organization.ui.committees.CommitteesView;
import cz.upce.gremis.gremisng.persons.ui.PersonsView;
import cz.upce.gremis.gremisng.voting.ui.VotesView;

@JsModule("prefers-color-scheme.js")
public class MainLayout extends AppLayout {

    private static final String H1_FONT_SIZE = "var(--lumo-font-size-l)";

    private String currentTheme = Lumo.DARK;

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Gremis");
        title.getStyle()
                .set("font-size", H1_FONT_SIZE)
                .set("margin", "0");

        VerticalLayout drawerHorizontalLayout = new VerticalLayout();
        drawerHorizontalLayout.setHeightFull();

        createTabs(drawerHorizontalLayout);

        /*Icon themeIcon = new Icon(VaadinIcon.ADJUST);
        themeIcon.setTooltipText("Změnit téma");
        themeIcon.addClickListener(e -> {
            Notification.show("Click");
            if (currentTheme.equals(Lumo.DARK)) {
               switchTheme(Lumo.LIGHT);
            }

            if (currentTheme.equals(Lumo.LIGHT)) {
                switchTheme(Lumo.DARK);
            }
        });

        drawerHorizontalLayout.add(themeIcon);
        drawerHorizontalLayout.setAlignSelf(FlexComponent.Alignment.END, themeIcon);*/

        addToDrawer(drawerHorizontalLayout);
        addToNavbar(toggle, title);

    }

    private void switchTheme(String newTheme) {
        var classList = UI.getCurrent().getElement().getClassList();
        classList.remove(currentTheme);
        classList.add(newTheme);
        currentTheme = newTheme;
    }

    private void createTabs(HasComponents drawerHorizontalLayout) {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.HOME, "Úvodní stránka", HomeView.class),
                createTab(VaadinIcon.DASHBOARD, "Grémia", BoardsView.class),
                createTab(VaadinIcon.HANDSHAKE, "Komise", CommitteesView.class),
                createTab(VaadinIcon.HANDS_UP, "Hlasování", VotesView.class),
                createTab(VaadinIcon.GROUP, "Osoby", PersonsView.class));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        drawerHorizontalLayout.add(tabs);
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends Component> viewClass) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setRoute(viewClass);

        link.setTabIndex(-1);

        return new Tab(link);
    }

}
