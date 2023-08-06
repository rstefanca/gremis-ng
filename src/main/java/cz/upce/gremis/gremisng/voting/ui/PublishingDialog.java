package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.upce.gremis.gremisng.time.BusinessTime;

import java.time.Duration;
import java.time.LocalDateTime;


public class PublishingDialog extends Dialog {

    private final TextField subject = new TextField("Předmět");

    private final TextField body = new TextField("Zpráva");

    private final DateTimePicker startAt = new DateTimePicker("Začátek");

    private final Button publishButton = new Button("Zveřejnit");

    private final Binder<PublishRequest> binder = new Binder<>(PublishRequest.class);

    private final LocalDateTime maxDateTime;

    private final VoteViewLogic viewLogic;

    private PublishRequest currentPublishRequest;

    public PublishingDialog(LocalDateTime maxDateTime, VoteViewLogic viewLogic, BusinessTime businessTime) {
        this.maxDateTime = maxDateTime;
        this.viewLogic = viewLogic;
        setHeaderTitle("Zveřejnit hlasování");

        var formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        subject.setValueChangeMode(ValueChangeMode.EAGER);
        body.setValueChangeMode(ValueChangeMode.EAGER);

        formLayout.add(subject, 2);
        formLayout.add(body,2);

        var minDateTime = computeMinDateTime(businessTime);
        startAt.setEnabled(false);
        startAt.setMin(minDateTime);
        startAt.setMax(maxDateTime);
        startAt.setStep(Duration.ofMinutes(15));

        var checkbox = new Checkbox("Zveřejnit ihned?", true);
        checkbox.addValueChangeListener(e -> {
            var enabled = Boolean.FALSE.equals(e.getValue());
            startAt.setEnabled(enabled);
            startAt.setValue(minDateTime);
            startAt.setRequiredIndicatorVisible(enabled);
        });

        formLayout.add(checkbox, startAt);

        add(formLayout);
        configureBinder();
        configureButtons();

        currentPublishRequest = new PublishRequest();
        binder.readBean(currentPublishRequest);
    }

    private static LocalDateTime computeMinDateTime(BusinessTime businessTime) {
       return businessTime.nextMinutes(15);
    }

    private void configureBinder() {
        binder.forField(subject)
                .asRequired()
                .bind(PublishRequest::getSubject, PublishRequest::setSubject);
        binder.forField(body)
                .asRequired()
                .bind(PublishRequest::getBody, PublishRequest::setBody);
        binder.forField(startAt)
                .bind(publishRequest -> publishRequest.getStartAt().orElse(null), PublishRequest::setStartAt);

        binder.addStatusChangeListener(e -> {
            var isValid = !e.hasValidationErrors();
            var hasChanges = binder.hasChanges();
            publishButton.setEnabled(hasChanges && isValid);
        });
    }

    private void configureButtons() {
        publishButton.addClickListener(e -> {
            if (binder.writeBeanIfValid(currentPublishRequest)) {
                viewLogic.publishVote(currentPublishRequest);
                close();
            }
        });
        publishButton.setEnabled(true);
        publishButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        publishButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> close());

        getFooter().add(publishButton, cancelButton);
    }
}
