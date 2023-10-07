package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

class BoardMenuBar extends Div {

    private final MenuItem edit;
    private final MenuItem committee;
    private final MenuItem perRollam;
    private final MenuItem term;
    private final MenuItem seat;

    public BoardMenuBar(BoardViewLogic viewLogic) {
        var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        edit = menuBar.addItem("Upravit");
        edit.addClickListener(e -> viewLogic.edit());

        var addMenuItem = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        term = addMenuItem.getSubMenu().addItem("Přidat funkční období");

        seat = addMenuItem.getSubMenu().addItem("Přidat funkci");
        committee = addMenuItem.getSubMenu().addItem("Přidat komisi");
        perRollam = addMenuItem.getSubMenu().addItem("Přidat hlasování");
        addMenuItem.getSubMenu().add(new Hr());
        addMenuItem.getSubMenu().addItem("Odstranit");

        term.addClickListener(e -> viewLogic.addTerm());
        committee.addClickListener(e -> viewLogic.addCommittee());

        add(menuBar);
    }

    void disableEditing() {
        edit.setEnabled(false);
        term.setEnabled(false);
        seat.setEnabled(false);
        committee.setEnabled(false);
        perRollam.setEnabled(false);
    }

    void enableEditing() {
        edit.setEnabled(true);
        term.setEnabled(true);
        seat.setEnabled(true);
        committee.setEnabled(true);
        perRollam.setEnabled(true);
    }

    void setCommitteeEnabled(boolean enabled) {
        committee.setEnabled(enabled);
    }

    void setPerRollamEnabled(boolean enabled) {
        perRollam.setEnabled(enabled);
    }

}
