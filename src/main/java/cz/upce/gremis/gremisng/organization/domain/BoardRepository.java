package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    //Page<Board> findAll(Pageable pageable);
    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.BoardListItemView(id, name, abbreviation, termType, active) FROM Board")
    Page<BoardListItemView> getBoardsPaginated(Pageable pageable);

    @Query("SELECT seats FROM Board where id = :id")
    Stream<BoardSeat> getSeats(Long id);

    @Query("SELECT committees FROM Board where id = :id")
    Stream<Committee> getCommittees(Long id);


}
