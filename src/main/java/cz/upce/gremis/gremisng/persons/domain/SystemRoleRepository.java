package cz.upce.gremis.gremisng.persons.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemRoleRepository extends JpaRepository<SystemRole, Long> {
}
