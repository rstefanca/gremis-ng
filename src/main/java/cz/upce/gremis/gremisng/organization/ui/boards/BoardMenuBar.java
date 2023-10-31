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
        var subMenu = addMenuItem.getSubMenu();

        term = subMenu.addItem("Přidat funkční období");
        seat = subMenu.addItem("Přidat funkci");
        committee = subMenu.addItem("Přidat komisi");
        perRollam = subMenu.addItem("Přidat hlasování");

        // Temporary
        subMenu.add(new Hr());
        var oldVersion = subMenu.addItem("Stará verze");
        oldVersion.addClickListener(e -> viewLogic.navigateToOldVersion());

        subMenu.add(new Hr());
        subMenu.addItem("Odstranit");

        term.addClickListener(e -> viewLogic.addTerm());
        seat.addClickListener(e -> viewLogic.addSeat());
        committee.addClickListener(e -> viewLogic.addCommittee());
        perRollam.addClickListener(e -> viewLogic.openNewVoting());

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
