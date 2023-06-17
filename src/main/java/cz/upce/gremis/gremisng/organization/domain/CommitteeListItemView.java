package cz.upce.gremis.gremisng.organization.domain;

public record CommitteeListItemView(
        long id,

        String name,

        String abbreviation,

        boolean mandatory,

        boolean workingGroupsEnabled,

        int durationInMonths,



        Long boardId,

        String boardName,

        boolean active) {

}
