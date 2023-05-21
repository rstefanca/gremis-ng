package cz.upce.gremis.gremisng.organization.ui;

public class BoardTermsFilter {

    private final Long boardTermId;
    private String name;

    public BoardTermsFilter(Long boardTermId) {
        this.boardTermId = boardTermId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBoardTermId() {
        return boardTermId;
    }
}
