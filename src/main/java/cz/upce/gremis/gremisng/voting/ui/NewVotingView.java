package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.ui.MainLayout;

@Route(value = "/new-voting", layout = MainLayout.class)
public class NewVotingView extends VerticalLayout {

    private final TextField name = new TextField("Název");

    private final DateTimePicker start = new DateTimePicker("Začátek");

    private final DateTimePicker end = new DateTimePicker("Konec");

    private final TextArea coverLetter = new TextArea("Průvodní slovo");

    private final TextArea note = new TextArea("Poznámka");


    public NewVotingView() {
        setWidthFull();
        setSpacing(true);
        var voteForm = new FormLayout();

        voteForm.add(name, 2);
        voteForm.add(start, end);
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

    }
}
