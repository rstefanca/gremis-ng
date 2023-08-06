package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.upce.gremis.gremisng.voting.domain.Resolution;
import cz.upce.gremis.gremisng.voting.domain.Vote;

import java.util.LinkedList;

class VoteAggregateForm extends Div {

    private final VerticalLayout content;

    private final VoteViewLogic voteViewLogic;


    private final VoteForm voteForm;

    private final Button addResolutionButton;

    private final Button saveButton;

    private Vote currentVote;

    private final Div resolutionCardsDiv;

    private final LinkedList<ResolutionCard> resolutionCards = new LinkedList<>();

    VoteAggregateForm(VoteViewLogic voteViewLogic) {
        this.voteViewLogic = voteViewLogic;
        content = new VerticalLayout();

        voteForm = new VoteForm();

        addResolutionButton = new Button();
        addResolutionButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        addResolutionButton.setIcon(new Icon(VaadinIcon.PLUS));
        addResolutionButton.addClickListener(e -> {
            var resolution = new Resolution();
            addResolutionCard(resolution);
            currentVote.addResolution(resolution);
        });

        resolutionCardsDiv = new Div();
        resolutionCardsDiv.setWidthFull();


        saveButton = new Button(getTranslation("buttons.save"));
        saveButton.addClickListener(e -> save());

        content.add(voteForm);
        content.add(new H2("Usnesení"));
        content.add(addResolutionButton);
        content.add(resolutionCardsDiv);

        content.add(saveButton);

        add(content);
    }

    private void save() {
        var validVote = voteForm.writeBeanIfValid(currentVote);
        var validResolutions = resolutionCards.stream().allMatch(ResolutionCard::writeIfValid);
        if (validVote && validResolutions) {
            voteViewLogic.saveVote(currentVote);
        }
    }

    void setVote(Vote vote) {
        currentVote = vote;
        voteForm.setVote(vote);
        vote.getResolutions().forEach(this::addResolutionCard);
    }

    void setReadOnly(boolean readOnly) {
        voteForm.setReadOnly(readOnly);
        addResolutionButton.setEnabled(!readOnly);
        saveButton.setEnabled(!readOnly);
        resolutionCards.forEach(it -> it.setReadOnly(readOnly));
    }

    void hideButtons() {
        addResolutionButton.setVisible(false);
        saveButton.setVisible(false);
    }

    void setBallotOptionVisible(boolean visible) {
        resolutionCards.forEach(resolutionCard -> resolutionCard.setBallotOptionsVisible(visible));
    }

    private void addResolutionCard(Resolution resolution) {
        var resolutionCard = new ResolutionCard();
        resolutionCard.setWidthFull();
        resolutionCard.setResolution(resolution);
        resolutionCards.add(resolutionCard);
        resolutionCardsDiv.add(resolutionCard);
        resolutionCard.addRemoveCardListener(e -> {
            //currentVote.removeResolution(e.getResolution());
            resolutionCardsDiv.remove(e.getSource());
            resolutionCards.remove(e.getSource());
            voteViewLogic.removeResolution(resolution);
        });
    }
}
