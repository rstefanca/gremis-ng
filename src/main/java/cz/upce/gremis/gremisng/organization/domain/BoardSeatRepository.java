package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardSeatRepository extends JpaRepository<BoardSeat, Long> {

    @Query("SELECT count(s) FROM BoardSeat s WHERE board.id = :boardId")
    int countBoardSeats(Long boardId);
}