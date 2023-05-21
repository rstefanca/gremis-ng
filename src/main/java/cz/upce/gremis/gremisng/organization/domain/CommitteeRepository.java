package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitteeRepository extends CrudRepository<Committee, Long> {

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.CommitteeListItemView(id, abbreviation, mandatory, workingGroupsEnabled, termDurationInMonths, order, board.id, board.name) " +
            "FROM Committee")
    Page<CommitteeListItemView> getCommitteesPaginated(Pageable pageable);

    @Query("SELECT count(c) FROM Committee c WHERE board.id = :boardId")
    int countCommittees(Long boardId);


}
