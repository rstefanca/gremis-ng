package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@org.springframework.stereotype.Repository
public interface BoardTermRepository extends JpaRepository<BoardTerm, Long> {

    @Query(value = "SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, description, start, end) " +
            "FROM BoardTerm where board.id = :boardId",
            countQuery = "SELECT count(*) FROM BoardTerm where board.id = :boardId")
    Page<TermDto> pageTerms(@Param("boardId") Long boardId, Pageable pageable);


    @Query(value = "SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, description, start, end) " +
            "FROM BoardTerm where board.id = :boardId")
    List<TermDto> getTerms( Long boardId);


    void delete(BoardTerm boardTerm);
}
