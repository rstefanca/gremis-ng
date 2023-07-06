package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface WorkingGroupRepository extends JpaRepository<WorkingGroup, Long> {
    long countByTermId(Long termId);

    WorkingGroup save(WorkingGroup workingGroup);

    Page<WorkingGroup> findByTermId(Long termId, Pageable pageable);


}
