package cz.upce.gremis.gremisng.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.gremis.gremisng.persons.domain.Person;
import cz.upce.gremis.gremisng.persons.service.PersonService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {
    private final AuthenticationContext authenticationContext;

    private final PersonService personService;

    SecurityService(AuthenticationContext authenticationContext,
                           PersonService personService) {
        this.authenticationContext = authenticationContext;
        this.personService = personService;
    }

    public UserDetails getAuthenticatedUserDetails() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
    }

    public void logout() {
        authenticationContext.logout();
    }
}
