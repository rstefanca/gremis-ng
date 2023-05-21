package cz.upce.gremis.gremisng.organization.service;

import cz.upce.gremis.gremisng.config.CacheNames;
import cz.upce.gremis.gremisng.organization.domain.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@Transactional(readOnly = true)
public class CommitteeAggregateService {

    private final CommitteeRepository committeeRepository;

    private final CommitteeTermRepository committeeTermRepository;


    CommitteeAggregateService(CommitteeRepository committeeRepository,
                              CommitteeTermRepository committeeTermRepository) {
        this.committeeRepository = committeeRepository;
        this.committeeTermRepository = committeeTermRepository;
    }

    public Committee getCommittee(CommitteeId committeeId) {
        return committeeRepository.findById(committeeId.value())
                .orElseThrow();
    }

    @Cacheable(CacheNames.COMMITTEE_TERMS_CACHE)
    public List<TermDto> getTermDtos(CommitteeId committeeId) {
       return committeeTermRepository.getTerms(committeeId.value());
    }

    @Transactional
    @CacheEvict(CacheNames.COMMITTEE_TERMS_CACHE)
    public void saveTerm(CommitteeTerm committeeTerm) {
        committeeTermRepository.save(committeeTerm);
        log.info("Saved committee term");
    }
}
