package cz.upce.gremis.gremisng.organization.domain;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CommitteeSeatRepository extends Repository<CommitteeSeat, Long> {

    Optional<CommitteeSeat> findById(Long id);

    @Transactional
    @Modifying
    @Query("delete from CommitteeSeat c where c.id = ?1")
    void deleteById(Long id);

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.CommitteeSeatDto(id, name, settings.filling, settings.grantedRole, order) " +
            "FROM CommitteeSeat where committee.id = :committeeId order by order")
    List<CommitteeSeatDto> getSeats(Long committeeId);

    @Query("SELECT count(c) FROM CommitteeSeat c WHERE c.committee.id = :committeeId")
    int countSeats(Long committeeId);


    CommitteeSeat save(CommitteeSeat seat);
}
