package cz.upce.gremis.gremisng.ui.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.data.binder.StatusChangeListener;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BinderUtils {

    public static StatusChangeListener createEnableButtonStatusChangeListener(Button button) {
        return e -> {
            var hasChanges = e.getBinder().hasChanges();
            var isValid = e.getBinder().isValid();
            button.setEnabled(hasChanges && isValid);
        };
    }
}
