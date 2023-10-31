package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
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
import cz.upce.gremis.gremisng.ui.components.NotificationHelper;
import cz.upce.gremis.gremisng.ui.utils.BinderUtils;
import cz.upce.gremis.gremisng.voting.domain.Resolution;
import cz.upce.gremis.gremisng.voting.domain.Vote;
import cz.upce.gremis.gremisng.voting.domain.VoteBoard;
import cz.upce.gremis.gremisng.voting.service.VoteAggregateService;
import jakarta.annotation.security.RolesAllowed;
import lombok.ToString;

import java.io.Serializable;

//http://localhost:8080/new-voting/1000/1000
@Route(value = "/new-voting/:boardId/:termId", layout = MainLayout.class)
@RolesAllowed({"ROOT"})
public class NewVotingView extends VerticalLayout implements BeforeEnterObserver {

    private final VoteAggregateService voteAggregateService;

    private final TextField name = new TextField("Název");

    //private final DateTimePicker start = new DateTimePicker("Začátek");

    private final DateTimePicker end = new DateTimePicker("Konec");

    private final TextArea coverLetter = new TextArea("Průvodní slovo");

    private final TextArea note = new TextArea("Poznámka");

    private final Binder<Vote> binder = new Binder<>(Vote .class);

    private final PageState state = new PageState();

    private int resolutionsCount;


    public NewVotingView(VoteAggregateService voteAggregateService) {
        this.voteAggregateService = voteAggregateService;

        setWidth("500px");
        setSpacing(true);

        var title = new H2("Definice nového hlasování");
        add(title);

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

        add(new H3("Usnesení"));

        add(new ResolutionsPanel(() -> {
            var resolutionForm = new ResolutionForm();
            resolutionForm.addStatusChangeListener(e -> {

            });
            return resolutionForm;
        }));

        var saveButton = new Button("Uložit", e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        add(saveButton);

        configureBinder();
        binder.addStatusChangeListener(BinderUtils.createEnableButtonStatusChangeListener(saveButton));

        addAttachListener(e -> {
            //add(new Span(state.toString()));
        });
    }

    private void configureBinder() {
        binder.forField(name)
                .asRequired()
                .bind(Vote::getName, Vote::setName);

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
            NotificationHelper.showSuccess("Uloženo");
            UI.getCurrent().navigate(VoteView.class, vote.getId());
        }
    }

    @ToString
    private static class PageState implements Serializable {

        private Long boardId;

        private Long termId;
    }
}
