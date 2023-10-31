package cz.upce.gremis.gremisng.organization.ui.boards.detail;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableConsumer;
import cz.upce.gremis.gremisng.organization.domain.Committee;
import cz.upce.gremis.gremisng.organization.ui.committees.CommitteeForm;
import cz.upce.gremis.gremisng.ui.components.ButtonFactory;
import cz.upce.gremis.gremisng.ui.utils.BinderUtils;

public class CommitteeFormDialog extends Dialog {

    public CommitteeFormDialog(Committee committee, SerializableConsumer<Dialog> saveAction) {
        setHeaderTitle(committee.isNew() ? "Nová komise" : "Upravit komisi");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        var committeeForm = new CommitteeForm();
        committeeForm.setItem(committee);
        add(committeeForm);

        var saveButton = ButtonFactory.createSaveButton(e -> {
            if (committeeForm.writeBeanIfValid(committee)) {
                saveAction.accept(this);
            }
        });

        committeeForm.addStatusChangeListener(BinderUtils.createEnableButtonStatusChangeListener(saveButton));

        getFooter().add(new Button(getTranslation("buttons.cancel"), e -> this.close()));
        getFooter().add(saveButton);
    }
}
