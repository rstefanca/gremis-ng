package cz.upce.gremis.gremisng.organization.domain;

public record CommitteeListItemView(
        long id,

        String name,

        String abbreviation,

        boolean mandatory,

        boolean workingGroupsEnabled,

        int durationInMonths,

        int order,

        String boardId,

        String boardName) {
}
