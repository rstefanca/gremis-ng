package cz.upce.gremis.gremisng.organization.domain;

public record BoardListItemView(
        long id,
        String name,
        String abbreviation,

        TermType termType,

        boolean active
        ) {
}
