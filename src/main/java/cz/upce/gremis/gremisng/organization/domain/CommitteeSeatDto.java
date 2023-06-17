package cz.upce.gremis.gremisng.organization.domain;

import cz.upce.gremis.gremisng.common.GremisRole;

public record CommitteeSeatDto (Long id, String name, Filling filling, GremisRole grantedRole, int order){
}
