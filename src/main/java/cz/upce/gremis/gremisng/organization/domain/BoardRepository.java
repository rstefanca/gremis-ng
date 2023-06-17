package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query(
            value = """
                    SELECT new cz.upce.gremis.gremisng.organization.domain.BoardListItemView(id, name, abbreviation, termType, active) FROM Board
                    WHERE (:name is null or upper(name) like upper(:searchTerm))
                    """,
            countQuery = """
                    SELECT count(id) from Board
                    WHERE (:name is null or upper(name) like upper(:searchTerm))
                    """)
    Page<BoardListItemView> getBoardsPaginated(@Param("searchTerm") @Nullable String searchTerm, Pageable pageable);

    @Query(
            value = """
                    select new cz.upce.gremis.gremisng.organization.domain.BoardNameDto(id, name) from Board
                    where (:searchTerm is null or upper(name) like upper(:searchTerm))
                    """,
            countQuery = """
                    select count(id) from Board
                    where (:searchTerm is null or upper(name) like upper(:searchTerm))
                    """
    )
    Page<BoardNameDto> selectNames(@Param("searchTerm") @Nullable String searchTerm, Pageable pageable);

    @Query("SELECT seats FROM Board where id = :id")
    Stream<BoardSeat> getSeats(Long id);

    @Query("SELECT committees FROM Board where id = :id")
    Stream<Committee> getCommittees(Long id);


}
