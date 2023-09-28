package cz.upce.gremis.gremisng.voting.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import cz.upce.gremis.gremisng.time.BusinessTime;
import cz.upce.gremis.gremisng.ui.MainLayout;
import cz.upce.gremis.gremisng.ui.components.ConfirmDeleteDialog;
import cz.upce.gremis.gremisng.voting.domain.Cancellation;
import cz.upce.gremis.gremisng.voting.domain.Resolution;
import cz.upce.gremis.gremisng.voting.domain.Vote;
import cz.upce.gremis.gremisng.voting.domain.VoteState;
import cz.upce.gremis.gremisng.voting.service.VoteAggregateService;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "/vote", layout = MainLayout.class)
@PermitAll
public class VoteView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private final VoteViewLogic viewLogic = new VoteViewLogic(this);

    private final VoteAggregateService service;

    private final BusinessTime businessTime;

    private final VoteMenu voteMenu;

    private final Component progressInfo;

    private final VoteAggregateForm voteForm;

    private Vote vote;

    public VoteView(VoteAggregateService service, BusinessTime businessTime) {
        this.service = service;
        this.businessTime = businessTime;
        setSizeFull();

        voteMenu = new VoteMenu(viewLogic);

        configureTop();

        progressInfo = inProgressInfo();
        add(progressInfo);

        // FORM here
        voteForm = new VoteAggregateForm(viewLogic);
        add(voteForm);

        add(new H3("Hodnocení"));
        add(new Text("Včerejší hlasování senátorů přineslo napínavý výsledek. Po dlouhých diskuzích a rozdílných názorech se podařilo dosáhnout kompromisu a přijmout nový zákon o ekonomických stimulech. I když výsledek neuspokojil úplně všechny, ukázal, že demokracie dokáže najít cestu. Nyní je klíčové sledovat, jaký vliv bude mít tato novela na ekonomiku země. Konečné rozhodnutí bylo přijato většinou jednoho hlasu."));

        add(new H3("Komentář schvalujicího"));
        add(new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in neque ut nibh aliquet convallis. Ut varius ex id metus."));

        viewLogic.init();
    }

    private void configureTop() {
        var top = new HorizontalLayout();
        top.setWidthFull();

        var title = new Span("Tady bude nazev hlasovani");
        top.add(title);

        top.add(voteMenu);

        top.expand(title);
        add(top);
    }

    private Component inProgressInfo() {
        var layout = new HorizontalLayout();
        layout.setId("progress_info");
        layout.add(new Text("Průběh hlasovnání "));
        var progressText = new Text("...");
        layout.add(progressText);
        var showProgressButton = new Button("Zobrazit průběh hlasování");
        showProgressButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        showProgressButton.addClickListener(e -> {
            var ballotsPreviewDialog = new BallotsPreviewDialog();
            ballotsPreviewDialog.setBallots(List.of());
            ballotsPreviewDialog.open();
        });
        layout.add(showProgressButton);
        layout.setVisible(false);
        return layout;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        vote = service.load(parameter);
        viewLogic.enter(vote);
    }

    @Override
    public String getPageTitle() {
        return vote.getName();
    }

    void setTitle(String title) {

    }

    void openCancelDialog() {
        var cancelDialog = new CancellationDialog(this.viewLogic);
        cancelDialog.open();
    }

    void cancelVote(Cancellation cancellation) {
        //TODO move to service
        vote.setCancelRationale(cancellation.getRationale());
        vote.setVoteState(VoteState.CANCELLED);
        service.save(vote);
    }

    void removeVote() {
        service.remove(vote);
    }

    public void showVote(Vote vote) {
        voteForm.setVote(vote);
    }

    public void editVote(Vote vote) {
        voteForm.setVote(vote);
        voteForm.setReadOnly(false);
    }

    void refresh() {
        voteForm.setVote(vote);
    }

    void showNotification(String message) {
        Notification.show(message);
    }

    void openRemoveDialog() {
        var dialog = new ConfirmDeleteDialog("Opravdu chcete smazat hlasování?");
        dialog.addConfirmListener(e -> {
            service.remove(vote);
            showNotification("Hlasovani smazano");
            UI.getCurrent().navigate(VotesView.class);
        });
        dialog.open();

    }

    void openPublishDialog() {
        var dialog = new PublishingDialog(
                vote.getEndTime().minusMinutes(60),
                viewLogic,
                businessTime);
        dialog.open();
    }

    void applyDraftState() {
        voteMenu.setRemoveEnabled(true);
        voteMenu.setCancelEnabled(false);
        //etc
    }

    void applyCancelledState() {
        voteMenu.setCancelEnabled(false);
        voteMenu.setPublishEnabled(false);
        hideProgressInfo();
        //etc
    }

    void applyOngoingState() {
        voteMenu.setCancelEnabled(true);
        voteMenu.setRemoveEnabled(false);
        voteMenu.setPublishEnabled(false);

        voteForm.setReadOnly(true);
        voteForm.hideButtons();

    }

    void applyScheduledState() {
        voteMenu.setPublishEnabled(false);
        voteMenu.setCancelEnabled(true);
        voteMenu.setRemoveEnabled(false);
    }

    void publishVote(PublishRequest publishRequest) {
        vote.setVoteState(publishRequest.isScheduled() ? VoteState.SCHEDULED : VoteState.ONGOING);
        vote.setStartTime(publishRequest.getStartAt().orElseGet(businessTime::getDateTime));
        vote = service.save(vote);
    }

    void showProgressInfo() {
        progressInfo.setVisible(true);
    }

    void hideProgressInfo() {
        progressInfo.setVisible(false);
    }

    public void removeResolution(Resolution resolution) {
        vote.removeResolution(resolution);
    }

    public void saveVote(Vote currentVote) {
        service.save(currentVote);
    }

    public void showBallotOptions() {
        voteForm.setBallotOptionVisible(true);
    }
}
