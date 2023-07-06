package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CommitteeTermRepository extends Repository<CommitteeTerm, Long> {

    long countByCommittee_Id(Long id);

    Optional<CommitteeTerm> findById(Long id);

    void deleteById(Long id);

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, name, start, end) " +
            "FROM CommitteeTerm where committee.id = :committeeId")
    @Transactional(readOnly = true)
    List<TermDto> getTerms(Long committeeId);

    CommitteeTerm save(CommitteeTerm committeeTerm);

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, name, start, end) " +
            "FROM CommitteeTerm where committee.id = :committeeId " +
            "AND :date >= start and :date <= end")
    Optional<TermDto> getTermForDate(Long committeeId, LocalDate date);


}
