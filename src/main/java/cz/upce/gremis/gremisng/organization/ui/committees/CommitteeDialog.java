package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import cz.upce.gremis.gremisng.organization.domain.Committee;

public class CommitteeDialog extends Dialog {

    private final CommitteeForm committeeForm;

    public CommitteeDialog() {
        committeeForm = new CommitteeForm();

        add(committeeForm);

        configureButtons();
    }

    private void configureButtons() {
        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> committeeForm.validateAndSave());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> committeeForm.cancel());

        getFooter().add(saveButton, cancelButton);
    }

    public CommitteeForm getForm() {
        return committeeForm;
    }

    public void setCommittee(Committee committee) {
        committeeForm.setItem(committee);
    }

}
