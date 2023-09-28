package cz.upce.gremis.gremisng.organization.ui.boards.detail;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import cz.upce.gremis.gremisng.organization.domain.Committee;
import cz.upce.gremis.gremisng.organization.ui.boards.BoardViewLogic;
import cz.upce.gremis.gremisng.organization.ui.committees.CommitteeForm;

import java.io.Serializable;

public class CommitteeFormDialog extends Dialog {

    private final BoardViewLogic viewLogic;
    private final CommitteeForm committeeForm;

    private SaveAction saveAction;

    public CommitteeFormDialog(BoardViewLogic viewLogic) {
        this.viewLogic = viewLogic;
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        committeeForm = new CommitteeForm();

        add(committeeForm);

        var saveButton = new Button(getTranslation("buttons.save"));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        saveButton.addClickListener(e -> saveAction.run());

        getFooter().add(new Button(getTranslation("buttons.cancel"), e -> {
            this.close();
        }));
        getFooter().add(saveButton);
    }

    public void addOnSaveClick(Runnable action) {
        this.saveAction = new SaveAction() {
            @Override
            public void run() {
                action.run();
            }
        };
    }

    public void setCommittee(Committee committee) {
        if (committee.isNew()) {
            setHeaderTitle("Nová komise");
        } else {
            setHeaderTitle("Upravit komisi");
        }
        committeeForm.setItem(committee);
    }

    public boolean writeBeanIfValid(Committee committee) {
        return committeeForm.writeBeanIfValid(committee);
    }

    public abstract static class SaveAction implements Runnable, Serializable {

    }
}
