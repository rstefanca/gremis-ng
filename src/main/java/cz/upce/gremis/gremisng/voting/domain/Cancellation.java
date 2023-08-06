package cz.upce.gremis.gremisng.voting.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class Cancellation {

    @NotNull
    private String subject;

    @NotNull
    private String body;

    @NotNull
    private String rationale;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}
