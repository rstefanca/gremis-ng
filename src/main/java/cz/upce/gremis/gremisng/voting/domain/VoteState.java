package cz.upce.gremis.gremisng.voting.domain;

public enum VoteState {
    DRAFT, //ADMIN
    SCHEDULED, //ADMIN
    ONGOING, //ADMIN
    CANCELLED, //?
    IN_PROCESS, //ADMIN, PRIVILEGED
    EVALUATED //MEMBER
}
