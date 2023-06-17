package cz.upce.gremis.gremisng.organization.ui.committees;


import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.common.GremisRole;
import cz.upce.gremis.gremisng.organization.domain.AssignmentType;
import cz.upce.gremis.gremisng.organization.domain.CommitteeSeat;
import cz.upce.gremis.gremisng.organization.domain.Filling;

public class CommitteeSeatDialog extends Dialog {

    private final Binder<CommitteeSeat> binder = new Binder<>(CommitteeSeat.class);

    private final TextField name = new TextField("Název");
    private final RadioButtonGroup<AssignmentType> assignmentType = new RadioButtonGroup<>();

    private final RadioButtonGroup<Filling> filling = new RadioButtonGroup<>();

    private final RadioButtonGroup<GremisRole> grantedRole = new RadioButtonGroup<>();


    private CommitteeSeat committeeSeat;

    public CommitteeSeatDialog() {

        configureForm();
        configureButtons();
        configureBinder();
    }

    private void configureForm() {
        var layout = new FormLayout();
        layout.setWidthFull();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        layout.add(name, 2);

        assignmentType.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        assignmentType.setLabel("Četnost funkce");
        assignmentType.setItems(AssignmentType.values());
        assignmentType.setItemLabelGenerator((ItemLabelGenerator<AssignmentType>) t ->
                getTranslation("enum.assignmentType." + t.name()));
        layout.add(assignmentType);

        filling.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        filling.setLabel("Obsazeni funkce");
        filling.setItems(Filling.values());
        filling.setItemLabelGenerator((ItemLabelGenerator<Filling>) t ->
                getTranslation("enum.filling." + t.name()));
        layout.add(filling);

        grantedRole.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        grantedRole.setLabel("Role");
        grantedRole.setItems(GremisRole.available());
        grantedRole.setItemLabelGenerator((ItemLabelGenerator<GremisRole>) Enum::name);
        layout.add(grantedRole);

        add(layout);
    }

    private void configureButtons() {
        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> validateAndSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> fireEvent(new CancelEvent(this, null))); //fire event


        getFooter().add(saveButton, cancelButton);
    }

    private void configureBinder() {
        binder.forField(name)
                .asRequired()
                .bind(CommitteeSeat::getName, CommitteeSeat::setName);

        binder.forField(assignmentType)
                .asRequired()
                .bind(s -> s.getSettings().getAssignmentType(), (s, value) -> s.getSettings().setAssignmentType(value));


        binder.forField(filling)
                .asRequired()
                .bind(s -> s.getSettings().getFilling(), (s, value) -> s.getSettings().setFilling(value));

        binder.forField(grantedRole)
                .bind(s -> s.getSettings().getGrantedRole(), (s, value) -> s.getSettings().setGrantedRole(value));
    }

    private void validateAndSave() {
        if (binder.writeBeanIfValid(committeeSeat)) {
            fireEvent(new SaveEvent(this, committeeSeat));
        }
    }

    public void setItem(CommitteeSeat item) {
        this.committeeSeat = item;
        binder.setBean(item);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    static abstract sealed class SeatDialogEvent extends ComponentEvent<CommitteeSeatDialog> {

        private final CommitteeSeat committeeSeat;

        public SeatDialogEvent(CommitteeSeatDialog source, CommitteeSeat committeeSeat) {
            super(source, false);
            this.committeeSeat = committeeSeat;
        }

        public CommitteeSeat getSeat() {
            return committeeSeat;
        }
    }

    public static final class SaveEvent extends SeatDialogEvent {

        public SaveEvent(CommitteeSeatDialog source, CommitteeSeat committeeSeat) {
            super(source, committeeSeat);
        }
    }

    public static final class CancelEvent extends SeatDialogEvent {

        public CancelEvent(CommitteeSeatDialog source, CommitteeSeat committeeSeat) {
            super(source, committeeSeat);
        }
    }
}
