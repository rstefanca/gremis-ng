package cz.upce.gremis.gremisng.persons.domain;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @NotNull Page<Person> findAll(@NotNull Pageable pageable);

    @Query("""
    select case when count(p) > 0 then true else false end from Person p
    where p.id <> :id and upper(p.login) = upper(:login)""")
    boolean checkLoginExists(@Param("id")Long excludeId, @Param("login") String login);

}
