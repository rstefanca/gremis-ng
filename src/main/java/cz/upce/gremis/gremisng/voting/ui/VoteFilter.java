package cz.upce.gremis.gremisng.voting.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

class VoteFilter implements Serializable {

    private String name;

    private final HashSet<Long> boardIds = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Long> getBoardIds() {
        return List.copyOf(boardIds);
    }

    void resetBoardIds() {
        boardIds.clear();
    }

    boolean hasBoardIds() {
        return !boardIds.isEmpty();
    }

    void addBoardId(Long boardId) {
        boardIds.add(boardId);
    }
}
