package cz.upce.gremis.gremisng.voting.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("select v from Vote v where v.voteState = ?1")
    Page<VoteDto> findByVoteState(VoteState voteState, Pageable pageable);
    //Page<VoteDto> findByVoteState(VoteState voteState, Pageable pageable);


    @Query(value = "select new cz.upce.gremis.gremisng.voting.domain.VoteDto(v.id, v.boardTermId, v.name, v.voteState, v.startTime, v.endTime, v.board.id, v.board.name) " +
            "from Vote v",
            countQuery = "select count(v.id) from Vote v")
    Page<VoteDto> page(Pageable pageable);
}
