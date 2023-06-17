package cz.upce.gremis.gremisng.organization.domain;

/**
 * Projection for {@link Committee}
 */
public interface CommitteeInfo {
    Long getId();

    String getName();

    String getAbbreviation();

    boolean isMandatory();

    int getMinimumSeats();

    boolean isWorkingGroupsEnabled();

    int getTermDurationInMonths();

    boolean isActive();

    BoardInfo getBoard();

    /**
     * Projection for {@link Board}
     */
    interface BoardInfo {
        Long getId();

        String getName();
    }

    default String getBoardName() {
        return getBoard().getName();
    }
}