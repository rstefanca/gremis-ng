package cz.upce.gremis.gremisng.organization.ui.committees;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class CommitteeFilter {
    private String searchTerm;

    private boolean activeOnly;

    private final HashSet<Long> boardIds = new HashSet<>();

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean isActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(boolean active) {
        this.activeOnly = active;
    }

    void addBoardId(Long boardId) {
        boardIds.add(boardId);
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
}
