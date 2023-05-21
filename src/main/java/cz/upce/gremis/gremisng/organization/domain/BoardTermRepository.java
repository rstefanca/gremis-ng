package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;


@org.springframework.stereotype.Repository
public interface BoardTermRepository extends Repository<BoardTerm, Long> {

    @Query(value = "SELECT new cz.upce.gremis.gremisng.organization.domain.TermDto(id, description, start, end) " +
            "FROM BoardTerm where board.id = :boardId",
            countQuery = "SELECT count(*) FROM BoardTerm where board.id = :boardId")
    Page<TermDto> pageTerms(@Param("boardId") Long boardId, Pageable pageable);

    void delete(BoardTerm boardTerm);
}
