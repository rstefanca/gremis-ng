package cz.upce.gremis.gremisng.organization.ui.boards;

import com.vaadin.flow.component.UI;
import cz.upce.gremis.gremisng.common.GremisRole;
import cz.upce.gremis.gremisng.organization.domain.Board;
import cz.upce.gremis.gremisng.organization.domain.BoardSeat;
import cz.upce.gremis.gremisng.organization.domain.TermDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class BoardViewLogic {

    private static final String EDIT_PREFIX = "* ";

    private final BoardView view;

    private Long boardId;

    BoardViewLogic(BoardView view) {
        this.view = view;
    }

    void init(UserDetails authenticatedUser) {
        var isRoot = new Authorities(authenticatedUser.getAuthorities()).is(GremisRole.ROOT);

        if (!isRoot) {
            view.disableEditing();
        }

    }

    public void enter(Board board) {
        // is there anything to do when we load the page?
        boardId = board.getId();

        refresh(board);
        view.setActive(board.isActive());
    }

    public void edit() {
        view.showEditDialog();
        updateTitle(true);
    }

    void updateTitle(boolean isEditing) {
        view.getUI()
                .map(UI::getPage)
                .ifPresent(page -> {
                    if (isEditing) {
                        page.setTitle(EDIT_PREFIX + view.getPageTitle());
                    } else {
                        page.setTitle(view.getPageTitle());
                    }
                });
    }

    public void refresh(Board board) {
        view.setBoard(board);
        view.setCommitteesEnable(board.isCommitteesEnabled());
        view.setPerRollamEnabled(board.isPerRollamEnabled());
    }

    public void selectTerm(TermDto term) {
        view.setCurrentTerm(term);
    }

    public void addTerm() {
        view.openTermDialog();
    }

    public void addCommittee() {
        view.openCommitteeDialog();
    }

    void navigateToOldVersion() {
        UI.getCurrent().navigate(BoardDetailView.class, boardId);
    }

    public void addSeat() {
        view.openSeatDialog(new BoardSeat());
    }

    public void openNewVoting() {
        view.navigateToNewVoting();
    }

    @RequiredArgsConstructor
    private static class Authorities {

        private final Collection<? extends GrantedAuthority> delegate;

        boolean is(GremisRole gremisRole) {
            var authority = "ROLE_" + gremisRole.name();
            return delegate.stream().anyMatch(it -> it.getAuthority().equals(authority));

        }
    }
}
