package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.upce.gremis.gremisng.voting.domain.Resolution;

// TODO add upload
class ResolutionCard extends Div  {

    private final HorizontalLayout layout;

    private final ResolutionForm form;

    private final Button removeButton;

    private final HorizontalLayout ballotOptionsDiv;


    private Resolution resolution;

    ResolutionCard() {
        setWidthFull();
        layout = new HorizontalLayout();
        layout.setWidthFull();
        form = new ResolutionForm();
        form.setWidthFull();
        removeButton = new Button();

        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        removeButton.setIcon(new Icon(VaadinIcon.TRASH));

        removeButton.addClickListener(e ->
                fireEvent(new RemoveCardEvent(this, resolution)));

        var leftPanel = new VerticalLayout();
        leftPanel.setWidthFull();
        leftPanel.addAndExpand(form);

        ballotOptionsDiv = new HorizontalLayout();
        ballotOptionsDiv.setWidthFull();
        ballotOptionsDiv.setSpacing(true);
        ballotOptionsDiv.setAlignItems(FlexComponent.Alignment.START);
        ballotOptionsDiv.add(createBallotOptionButton("Shouhlasím"));
        ballotOptionsDiv.add(createBallotOptionButton("Nesouhlasím"));
        ballotOptionsDiv.add(createBallotOptionButton("Zdržuji se"));
        ballotOptionsDiv.setVisible(false);
        leftPanel.addAndExpand(ballotOptionsDiv);

        layout.add(leftPanel, removeButton);
        layout.expand(leftPanel);
        layout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, leftPanel);
        layout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, removeButton);
        add(layout);
    }

    void setResolution(Resolution resolution) {
        form.setResolution(resolution);
        this.resolution = resolution;
    }

    void setReadOnly(boolean readOnly) {
        removeButton.setVisible(!readOnly);
        form.setReadOnly(readOnly);
    }

    void setBallotOptionsVisible(boolean visible) {
        ballotOptionsDiv.setVisible(visible);
    }

    boolean writeIfValid() {
        return form.writeIfValid(resolution);
    }

    Registration addRemoveCardListener(ComponentEventListener<RemoveCardEvent> listener) {
       return addListener(RemoveCardEvent.class, listener);
    }

    private Button createBallotOptionButton(String label) {
        var button = new Button(label);
        button.addThemeVariants(ButtonVariant.LUMO_LARGE);
        button.addClassName(LumoUtility.Padding.Right.MEDIUM);

        return button;
    }

    static class RemoveCardEvent extends ComponentEvent<ResolutionCard> {

        private final Resolution resolution;

        public RemoveCardEvent(ResolutionCard source, Resolution resolution) {
            super(source, false);
            this.resolution = resolution;
        }

        public Resolution getResolution() {
            return resolution;
        }
    }
}
