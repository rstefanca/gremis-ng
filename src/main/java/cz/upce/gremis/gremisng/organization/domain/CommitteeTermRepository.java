package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface CommitteeTermRepository extends Repository<CommitteeTerm, Long> {

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, name, start, end) " +
            "FROM CommitteeTerm where committee.id = :committeeId")
    List<TermDto> getTerms(Long committeeId);

    CommitteeTerm save(CommitteeTerm committeeTerm);
}
