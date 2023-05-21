package cz.upce.gremis.gremisng.assignments.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;
@Repository
public interface BoardSeatAssignmentRepository extends JpaRepository<BoardSeatAssignment, Long> {

    @Query("select a from BoardSeatAssignment a join fetch a.person p join fetch a.seat s where a.term.id = :id")
    Stream<BoardSeatAssignment> findByTermId(Long id);

    @EntityGraph(attributePaths = {"person", "seat"})
    Page<BoardSeatAssignment> findByTerm_Id(Long termId, Pageable pageable);
}
