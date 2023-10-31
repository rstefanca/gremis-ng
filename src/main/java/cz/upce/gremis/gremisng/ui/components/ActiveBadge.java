package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.html.Span;

public class ActiveBadge extends Span {

    private static final String COMMON_STATE_DISABLED = "common.state.disabled";
    private static final String COMMON_STATE_ACTIVE = "common.state.active";
    private static final String BADGE_CONTRAST = "badge contrast";
    private static final String BADGE_SUCCESS = "badge success";

    public void setActive(boolean active) {
        if (!active) {
            setText(getTranslation(COMMON_STATE_DISABLED));
            getElement().getThemeList().add(BADGE_CONTRAST);
        } else {
            setText(getTranslation(COMMON_STATE_ACTIVE));
            getElement().getThemeList().add(BADGE_SUCCESS);
        }
    }
}
