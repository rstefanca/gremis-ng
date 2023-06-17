package cz.upce.gremis.gremisng.assignments.service;

import cz.upce.gremis.gremisng.assignments.domain.CommitteeSeatAssignment;
import cz.upce.gremis.gremisng.assignments.domain.CommitteeSeatAssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommitteeSeatAssignmentService {

    private final CommitteeSeatAssignmentRepository repository;

    public CommitteeSeatAssignmentService(CommitteeSeatAssignmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(CommitteeSeatAssignment assignment) {
        assignment.setName(assignment.getSeat().getName());
        repository.save(assignment);
    }


    public Page<CommitteeSeatAssignment> findByTermId(Long termId, Pageable pageable) {
        return repository.findByTerm_Id(termId, pageable);
    }
}
