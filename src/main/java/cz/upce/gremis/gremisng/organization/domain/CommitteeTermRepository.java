package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CommitteeTermRepository extends Repository<CommitteeTerm, Long> {

    Optional<CommitteeTerm> findById(Long id);

    void deleteById(Long id);

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, name, start, end) " +
            "FROM CommitteeTerm where committee.id = :committeeId")
    @Transactional(readOnly = true)
    List<TermDto> getTerms(Long committeeId);

    CommitteeTerm save(CommitteeTerm committeeTerm);
}
