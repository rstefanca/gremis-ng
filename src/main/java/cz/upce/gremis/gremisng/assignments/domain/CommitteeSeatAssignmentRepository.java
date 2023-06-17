package cz.upce.gremis.gremisng.assignments.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeSeatAssignmentRepository extends JpaRepository<CommitteeSeatAssignment, Long> {

    @EntityGraph(attributePaths = {"person"})
    Page<CommitteeSeatAssignment> findByTerm_Id(Long termId, Pageable pageable);
}
