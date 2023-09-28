package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

class BoardMenuBar extends Div {

    private final BoardViewLogic viewLogic;

    private final MenuBar menuBar;

    private final MenuItem edit;
    private final MenuItem committee;
    private final MenuItem perRollam;

    public BoardMenuBar(BoardViewLogic viewLogic) {
        this.viewLogic = viewLogic;

        menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        edit = menuBar.addItem("Upravit");
        edit.addClickListener(e -> viewLogic.edit());

        var addMenuItem = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        var addTerm = addMenuItem.getSubMenu().addItem("Přidat funkční období");

        addMenuItem.getSubMenu().addItem("Přidat funkci");
        committee = addMenuItem.getSubMenu().addItem("Přidat komisi");
        perRollam = addMenuItem.getSubMenu().addItem("Přidat hlasování");
        addMenuItem.getSubMenu().add(new Hr());
        addMenuItem.getSubMenu().addItem("Odstranit");

        addTerm.addClickListener(e -> viewLogic.addTerm());
        committee.addClickListener(e -> viewLogic.addCommittee());

        add(menuBar);
    }

    void disableEditing() {
        edit.setEnabled(false);
    }

    void setCommitteeEnabled(boolean enabled) {
        committee.setEnabled(enabled);
    }

    void setPerRollamEnabled(boolean enabled) {
        perRollam.setEnabled(enabled);
    }

}
