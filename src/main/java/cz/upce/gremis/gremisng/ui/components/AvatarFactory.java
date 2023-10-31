package cz.upce.gremis.gremisng.ui.components;

import com.vaadin.flow.component.avatar.Avatar;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AvatarFactory {

    public static Avatar createOrderAvatar(int order) {
        return new Avatar(String.valueOf(order));
    }
}
