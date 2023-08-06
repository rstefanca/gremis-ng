package cz.upce.gremis.gremisng.voting.service;

import cz.upce.gremis.gremisng.voting.domain.Vote;
import cz.upce.gremis.gremisng.voting.domain.VoteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class VoteAggregateService {

    private final VoteRepository voteRepository;

    VoteAggregateService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Vote save(Vote vote) {
       return voteRepository.save(vote);
    }

    public Vote load(Long id) {
        return voteRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void remove(Vote vote) {
        voteRepository.delete(vote);
    }
}
