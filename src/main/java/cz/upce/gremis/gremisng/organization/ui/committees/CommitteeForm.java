package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import cz.upce.gremis.gremisng.organization.domain.Committee;


public class CommitteeForm extends FormLayout {

    private final Binder<Committee> binder = new Binder<>(Committee.class);

    private final TextField name = new TextField("Název");

    private final TextField abbreviation = new TextField("Zkratka");

    private final Checkbox mandatory = new Checkbox("Povinná komise", true);

    private final NumberField duration = new NumberField("Doba trvání v měsících");

    private final NumberField minimumSeats = new NumberField("Minimální počet členů");

    private final Checkbox workingGroupsEnabled = new Checkbox("Pracovní skupiny");

    private Committee item;

    public CommitteeForm() {
        setWidthFull();
        setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        configureForm();
        configureBinding();
    }

    private void configureForm() {
        duration.setStepButtonsVisible(true);
        duration.setMin(1);
        duration.setStep(1);

        duration.setAllowedCharPattern("\\d");
        minimumSeats.setStepButtonsVisible(true);
        minimumSeats.setMin(1);
        minimumSeats.setStep(1);
        minimumSeats.setValue(1d);
        minimumSeats.setAllowedCharPattern("\\d");
        add(name, abbreviation, duration, minimumSeats, mandatory, workingGroupsEnabled);

    }

    private void configureBinding() {
        binder.forField(name)
                .asRequired()
                .bind(Committee::getName, Committee::setName);

        binder.forField(abbreviation)
                .asRequired()
                .bind(Committee::getAbbreviation, Committee::setAbbreviation);

        binder.forField(duration)
                .asRequired()
                .withConverter(Double::intValue, Double::valueOf)
                .bind(Committee::getTermDurationInMonths, Committee::setTermDurationInMonths);

        binder.forField(minimumSeats)
                .asRequired()
                .withConverter(Double::intValue, Double::valueOf)
                .bind(Committee::getMinimumSeats, Committee::setMinimumSeats);

        binder.forField(workingGroupsEnabled)
                .bind(Committee::isWorkingGroupsEnabled, Committee::setWorkingGroupsEnabled);
    }

    void validateAndSave() {
        var validationStatus = binder.writeBeanIfValid(item);
        if (validationStatus) {
            fireEvent(new SaveEvent(this, item));
        } else {
            //deal with errors
            Notification.show("Fix errors");
        }
    }

    void cancel() {
        fireEvent(new CommitteeForm.CancelEvent(this));
    }

    public void setItem(Committee item) {
        binder.readBean(item);
        if (item != null && item.isNew()) {
            setDefaults();
        }

        this.item = item;
    }

    private void setDefaults() {
        duration.setValue(12d);
        minimumSeats.setValue(1d);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    public static sealed abstract class CommitteeFormEvent extends ComponentEvent<CommitteeForm> {

        private final Committee committee;

        protected CommitteeFormEvent(CommitteeForm source, Committee committee) {
            super(source, false);
            this.committee = committee;
        }

        public Committee getCommittee() {
            return committee;
        }
    }

    public static final class SaveEvent extends CommitteeFormEvent {

        SaveEvent(CommitteeForm source, Committee committee) {
            super(source, committee);
        }
    }

    public static final class CancelEvent extends CommitteeFormEvent {

        public CancelEvent(CommitteeForm source) {
            super(source, null);
        }
    }

}
