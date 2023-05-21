package cz.upce.gremis.gremisng.persons.service;

import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.domain.PersonRepository;
import cz.upce.gremis.gremisng.persons.domain.SystemRole;
import cz.upce.gremis.gremisng.persons.domain.SystemRoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Log4j2
@Component
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository personRepository;
    private final SystemRoleRepository systemRoleRepository;

    PersonService(PersonRepository personRepository, SystemRoleRepository systemRoleRepository) {
        this.personRepository = personRepository;
        this.systemRoleRepository = systemRoleRepository;
    }

    public Person getPerson(Long id) {
        return personRepository.getReferenceById(id);
    }

    public void loadPerson(Long id, Consumer<Person> consumer) {
        var person = getPerson(id);
        consumer.accept(person);
    }

    @Transactional
    public void updatePerson(Long id, Consumer<Person> updateAction) {
        var person = personRepository.getReferenceById(id);
        updateAction.accept(person);
        log.info("Saving person - id: {}", person);
        personRepository.save(person);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSystemRole(SystemRole systemRole) {
        systemRoleRepository.save(systemRole);
        log.info("Saved system role - id: {}", systemRole.getId());
    }


    public boolean checkLoginExists(Long exceptId, String login) {
        return personRepository.checkLoginExists(exceptId, login);
    }
}
