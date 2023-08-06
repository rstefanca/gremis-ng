package cz.upce.gremis.gremisng.voting.ui;

import cz.upce.gremis.gremisng.voting.domain.Cancellation;
import cz.upce.gremis.gremisng.voting.domain.Resolution;
import cz.upce.gremis.gremisng.voting.domain.Vote;

class VoteViewLogic {

    private final VoteView view;

    VoteViewLogic(VoteView view) {
        this.view = view;
    }

    void init() {

    }

    void enter(Vote vote) {
        view.showVote(vote);
        switch (vote.getVoteState()) {
            case DRAFT -> view.applyDraftState();
            case SCHEDULED -> view.applyScheduledState();
            case ONGOING -> {
                view.applyOngoingState();
                if (isMember()) {
                    view.showBallotOptions();
                }
                view.showProgressInfo(); // or hide
            }
            case CANCELLED -> view.applyCancelledState();
            default -> {
            } //do nothing yet
        }
    }

    public void cancelVote(Cancellation cancellation) {
        view.cancelVote(cancellation);
        view.applyCancelledState();
        view.showNotification("Hlasovani zruseno!");
    }

    public void publishVote(PublishRequest publishRequest) {
        view.publishVote(publishRequest);


        if (publishRequest.isScheduled()) {
            view.applyScheduledState();
        } else {
            view.applyOngoingState();
        }

        view.showNotification("Zveřejněno");

    }

    public void openCancelDialog() {
        //if vote.isInProgress...

        view.openCancelDialog();
    }

    public void openPublishDialog() {
        view.openPublishDialog();
    }

    public void openRemoveDialog() {
        view.openRemoveDialog();
    }


    public void saveVote(Vote currentVote) {
        view.saveVote(currentVote);
        view.showNotification("Uloženo");
    }

    public void removeResolution(Resolution resolution) {
        view.removeResolution(resolution);
    }

    private boolean isMember() {
        return true;
    }
}
