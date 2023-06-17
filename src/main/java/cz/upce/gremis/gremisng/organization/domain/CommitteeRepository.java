package cz.upce.gremis.gremisng.organization.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommitteeRepository extends CrudRepository<Committee, Long> {
    @Query("select c from Committee c where c.active = true")
    List<Committee> findByActiveTrue();

    @Query("""
            select c from Committee c
            where (:name is null or upper(c.name) like upper(:name))
            or (:abbreviation is null or upper(c.abbreviation) like upper(:abbreviation))""")
    Page<Committee> findByNameLikeOrAbbreviationLike(@Param("name") @Nullable String name, @Param("abbreviation") @Nullable String abbreviation, Pageable pageable);

    @Query("""
            select c from Committee c
            where c.active = true and
            ((:name is null or upper(c.name) like upper(:name))
            or (:abbreviation is null or upper(c.abbreviation) like upper(:abbreviation)))""")
    Page<Committee> findActiveByNameLikeOrAbbreviationLike(@Param("name") @Nullable String name, @Param("abbreviation") @Nullable String abbreviation, Pageable pageable);

    //:#{#categoryIds == null} = true
    @Query("""
            select c from Committee c
            where 
                (:searchTerm is null or upper(c.name) like upper(:searchTerm) or upper(c.abbreviation) like upper(:searchTerm))
                and (:active is null or c.active = true)
                and (:#{#boardIds == null} = true or c.board.id in :boardIds)""")
    Page<Committee> complexQuery(@Param("searchTerm") String searchTerm, Boolean active, List<Long> boardIds, Pageable pageable);

    Page<Committee> findAll(Pageable pageable);

    @Query("SELECT new cz.upce.gremis.gremisng.organization.domain.CommitteeListItemView(id, abbreviation, mandatory, workingGroupsEnabled, termDurationInMonths, order, board.id, board.name, active) " +
            "FROM Committee")
    Page<CommitteeListItemView> getCommitteesPaginated(Pageable pageable);

    @Query("SELECT count(c) FROM Committee c WHERE board.id = :boardId")
    int countCommittees(Long boardId);


}
