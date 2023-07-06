package cz.upce.gremis.gremisng.organization.service;

import cz.upce.gremis.gremisng.config.CacheNames;
import cz.upce.gremis.gremisng.organization.domain.*;
import cz.upce.gremis.gremisng.time.BusinessTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional(readOnly = true)
public class CommitteeAggregateService {

    private final CommitteeRepository committeeRepository;

    private final CommitteeTermRepository committeeTermRepository;

    private final CommitteeSeatRepository committeeSeatRepository;

    private final WorkingGroupRepository workingGroupRepository;

    private final BusinessTime businessTime;


    CommitteeAggregateService(CommitteeRepository committeeRepository,
                              CommitteeTermRepository committeeTermRepository,
                              CommitteeSeatRepository committeeSeatRepository,
                              WorkingGroupRepository workingGroupRepository,
                              BusinessTime businessTime) {
        this.committeeRepository = committeeRepository;
        this.committeeTermRepository = committeeTermRepository;
        this.committeeSeatRepository = committeeSeatRepository;
        this.workingGroupRepository = workingGroupRepository;
        this.businessTime = businessTime;
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

    @Transactional
    public void removeTerm(Long id) {
        committeeTermRepository.deleteById(id);
        log.info("Term removed - id={}", id);
    }

    @Transactional
    public void saveSeat(CommitteeSeat committeeSeat) {
        committeeSeatRepository.save(committeeSeat);
        log.info("Saved committee seat");
    }

    public List<CommitteeSeatDto> getSeatDtos(CommitteeId committeeId) {
        return committeeSeatRepository.getSeats(committeeId.value());
    }

    @Transactional
    public void updateSeatsOrder(CommitteeId id, List<Long> orderedIds) {
        Committee committee = committeeRepository.findById(id.value()).orElseThrow();
        committee.getSeats()
                .forEach(seat -> {
                    var priorityOrder = orderedIds.indexOf(seat.getId()) + 1;
                    seat.setOrder(priorityOrder);
                });
        committeeRepository.save(committee);
        log.info("Updated seats - committeeId = {}", id.value());
    }

    @Transactional
    public void removeSeat(Long id) {
        committeeSeatRepository.deleteById(id);
        log.info("Seat removed - id = {}", id);
    }

    public CommitteeTerm getTerm(Long id) {
        return committeeTermRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void saveWorkingGroup(WorkingGroup workingGroup) {
        workingGroupRepository.save(workingGroup);
        log.info("Saved working group");
    }

    public int countTerms(CommitteeId committeeId) {
        return (int) committeeTermRepository.countByCommittee_Id(committeeId.value());
    }

    public Optional<TermDto> getCurrentTerm(CommitteeId committeeId) {
        return committeeTermRepository.getTermForDate(committeeId.value(), businessTime.getDate());
    }
}
