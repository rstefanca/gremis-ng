package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;


public class VoteMenu extends Div {

    private final MenuBar content;

    private final MenuItem cancelMenuItem;

    private final MenuItem publishMenuItem;

    private final MenuItem removeMenuItem;

    public VoteMenu(VoteViewLogic viewLogic) {
        this.content = new MenuBar();

        cancelMenuItem = content.addItem("Zrusit", event -> viewLogic.openCancelDialog());
        publishMenuItem = content.addItem("Zverenit", event -> viewLogic.openPublishDialog());
        removeMenuItem = content.addItem(new Icon(VaadinIcon.TRASH));
        removeMenuItem.add(getTranslation("buttons.remove"));
        removeMenuItem.addClickListener(click -> viewLogic.openRemoveDialog());

        add(content);
    }

    void setCancelEnabled(boolean enabled) {
        cancelMenuItem.setEnabled(enabled);
    }

    void setPublishEnabled(boolean enabled) {
        publishMenuItem.setEnabled(enabled);
    }

    void setRemoveEnabled(boolean enabled) {
        removeMenuItem.setEnabled(enabled);
    }


}
