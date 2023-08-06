package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.voting.domain.Resolution;
import cz.upce.gremis.gremisng.voting.domain.Vote;
import cz.upce.gremis.gremisng.voting.domain.VoteBoard;
import cz.upce.gremis.gremisng.voting.service.VoteAggregateService;
import lombok.ToString;

import java.io.Serializable;

//http://localhost:8080/new-voting/1000/1000
@Route(value = "/new-voting/:boardId/:termId", layout = MainLayout.class)
public class NewVotingView extends VerticalLayout implements BeforeEnterObserver {

    private final VoteAggregateService voteAggregateService;

    private final TextField name = new TextField("Název");

    //private final DateTimePicker start = new DateTimePicker("Začátek");

    private final DateTimePicker end = new DateTimePicker("Konec");

    private final TextArea coverLetter = new TextArea("Průvodní slovo");

    private final TextArea note = new TextArea("Poznámka");

    private final Binder<Vote> binder = new Binder<>(Vote .class);

    private final PageState state = new PageState();


    public NewVotingView(VoteAggregateService voteAggregateService) {
        this.voteAggregateService = voteAggregateService;

        setWidthFull();
        setSpacing(true);
        var voteForm = new FormLayout();

        voteForm.add(name, 2);
        //voteForm.add(start, end);
        voteForm.add(end);
        voteForm.add(coverLetter, 2);
        voteForm.add(note, 2);

        coverLetter.setMaxLength(400);
        coverLetter.setClearButtonVisible(true);
        coverLetter.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 400));
        note.setMaxLength(400);
        note.setClearButtonVisible(true);
        note.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 400));


        add(voteForm);

        add(new Span("Podklady"));

        MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
        Upload multiFileUpload = new Upload(multiFileMemoryBuffer);
        multiFileUpload.setDropAllowed(true);
        multiFileUpload.setAutoUpload(false);

        add(multiFileUpload);

        var resolutionForm = new FormLayout();
        resolutionForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        resolutionForm.add(new TextField("Usnesení"));
        resolutionForm.add(new TextArea("Poznámka"));


        add(resolutionForm);
        add(new Button("Uložit", e -> save()));

        configureBinder();

        addAttachListener(e -> {
            add(new Span(state.toString()));
        });
    }

    private void configureBinder() {
        binder.forField(name)
                .asRequired()
                .bind(Vote::getName, Vote::setName);

       /* binder.forField(start)
                .bind(Vote::getStartTime, Vote::setStartTime);*/

        binder.forField(end)
                .asRequired()
                .bind(Vote::getEndTime, Vote::setEndTime);

        binder.forField(coverLetter)
                .asRequired()
                .bind(Vote::getCoverLetter, Vote::setCoverLetter);

        binder.forField(note)
                .bind(Vote::getNote, Vote::setNote);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        state.boardId = event.getRouteParameters().getLong("boardId").get();
        state.termId = event.getRouteParameters().getLong("termId").get();
    }

    private void save() {
        var vote = new Vote();
        if (binder.writeBeanIfValid(vote)) {
            vote.setBoard(new VoteBoard(state.boardId));
            vote.setBoardTermId(state.termId);

            // temp
            for (int i = 1; i <= 2; i++) {
                var resolution = new Resolution();
                resolution.setText("Resolution " + i);
                resolution.setNote("Note");
                resolution.setOrder(1);
                vote.addResolution(resolution);
            }

            //--------

            voteAggregateService.save(vote);
            Notification.show("Uloženo");
            UI.getCurrent().navigate(VoteView.class, vote.getId());
        }
    }

    @ToString
    private static class PageState implements Serializable {

        private Long boardId;

        private Long termId;
    }
}
