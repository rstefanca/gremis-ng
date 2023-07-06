package cz.upce.gremis.gremisng.organization.ui.committees;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import cz.upce.gremis.gremisng.organization.domain.TermDto;
import cz.upce.gremis.gremisng.organization.domain.WorkingGroup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkingGroupDialog extends Dialog {

    private final Binder<WorkingGroup> binder = new Binder<>(WorkingGroup.class);

    private final CommitteeTermsComboBox termsComboBox;

    private final TextField name = new TextField("Název");

    private final DatePicker from = new DatePicker("Začátek");

    private final DatePicker to = new DatePicker("Konec");

    WorkingGroupDialog(CommitteeTermsComboBox termsComboBox) {
        this.termsComboBox = termsComboBox;

        configureForm();
        configureBinder();
        configureButtons();

        setHeaderTitle("Pracovní skupina");

        addDialogCloseActionListener(e -> reset());
    }

    private void configureForm() {
        var layout = new FormLayout();
        layout.setWidthFull();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        layout.add(termsComboBox, 2);
        layout.add(name, 2);
        layout.add(from, to);

        add(layout);
    }

    private void configureBinder() {
        binder.forField(termsComboBox)
                .asRequired()
                .withConverter(new Converter<TermDto, Long>() {
                    @Override
                    public Result<Long> convertToModel(TermDto value, ValueContext context) {
                        return Result.ok(value.id());
                    }

                    @Override
                    public TermDto convertToPresentation(Long value, ValueContext context) {
                        return termsComboBox.getItemById(value).orElseThrow();
                    }
                })
                .bind(WorkingGroup::getTermId, WorkingGroup::setTermId);

        binder.forField(name)
                .asRequired()
                .bind(WorkingGroup::getName, WorkingGroup::setName);

        binder.forField(from)
                .asRequired()
                .bind(WorkingGroup::getStart, WorkingGroup::setStart);

        binder.forField(to)
                .asRequired()
                .bind(WorkingGroup::getEnd, WorkingGroup::setEnd);
    }

    private void configureButtons() {
        var saveButton = new Button(
                getTranslation("buttons.save"),
                click -> validateAndSave());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> {
                    binder.removeBean();
                    binder.refreshFields();
                    close();
                    fireEvent(new CancelEvent(this));
                }); //fire event


        getFooter().add(saveButton, cancelButton);
    }

    private void validateAndSave() {
        var workingGroup = new WorkingGroup();
        if (binder.writeBeanIfValid(workingGroup)) {
            fireEvent(new SaveEvent(this, workingGroup));
        }
    }

    void setCommitteeId(Long committeeId) {
        termsComboBox.setCommitteeId(committeeId);
    }

    void setSelectedTermId(Long termId) {
        termsComboBox.setSelectedTermId(termId);
    }

    void reset() {
        binder.removeBean();
        binder.refreshFields();
    }


    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CancelEvent> listener) {
        return addListener(CancelEvent.class, listener);
    }

    static abstract sealed class WorkingGroupDialogEvent extends ComponentEvent<WorkingGroupDialog> {

        private final WorkingGroup workingGroup;

        public WorkingGroupDialogEvent(WorkingGroupDialog source, WorkingGroup workingGroup) {
            super(source, false);
            this.workingGroup = workingGroup;
        }

        public WorkingGroup getWorkingGroup() {
            return workingGroup;
        }
    }

    public static final class SaveEvent extends WorkingGroupDialogEvent {

        public SaveEvent(WorkingGroupDialog source, WorkingGroup workingGroup) {
            super(source, workingGroup);
        }
    }

    public static final class CancelEvent extends WorkingGroupDialogEvent {

        public CancelEvent(WorkingGroupDialog source) {
            super(source, null);
        }
    }

}
