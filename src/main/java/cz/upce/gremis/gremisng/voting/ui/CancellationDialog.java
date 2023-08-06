package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.upce.gremis.gremisng.voting.domain.Cancellation;

public class CancellationDialog extends Dialog {

    private final VoteViewLogic voteViewLogic;

    private final Binder<Cancellation> binder = new Binder<>(Cancellation.class);

    private final TextField subject;

    private final TextArea body;

    private final TextArea rationale;

    private Button saveButton;


    public CancellationDialog(VoteViewLogic voteViewLogic) {
        this.voteViewLogic = voteViewLogic;

        setHeaderTitle("Zrušení hlasovnání");

        var layout = new VerticalLayout();

        subject = new TextField("Předmět");
        subject.setValueChangeMode(ValueChangeMode.EAGER);

        body = new TextArea("Zpráva");
        body.setValueChangeMode(ValueChangeMode.EAGER);

        rationale = new TextArea("Odůvodnění");
        rationale.setWidthFull();
        rationale.setValueChangeMode(ValueChangeMode.EAGER);

        layout.add(subject, body, rationale);
        add(layout);

        binder.forField(subject)
                .asRequired()
                .bind("subject");
        binder.forField(body)
                .asRequired()
                .bind("body");
        binder.forField(rationale)
                .asRequired()
                .bind("rationale");

        binder.addStatusChangeListener(e -> {
            var isValid = !e.hasValidationErrors();
            var hasChanges = binder.hasChanges();
            saveButton.setEnabled(hasChanges && isValid);
        });

        configureButtons();
    }

    private void configureButtons() {
        saveButton = new Button(
                getTranslation("buttons.save"),
                click -> {
                    var cancellation = new Cancellation();
                    if (binder.writeBeanIfValid(cancellation)) {
                        voteViewLogic.cancelVote(cancellation);
                        close();
                    }

                });
        saveButton.setEnabled(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.getStyle().set("margin-right", "auto");

        var cancelButton = new Button(
                getTranslation("buttons.cancel"),
                click -> close());


        getFooter().add(saveButton, cancelButton);
    }


}
