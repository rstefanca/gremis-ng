package cz.upce.gremis.gremisng.security;

import cz.upce.gremis.gremisng.persons.domain.PersonRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UserDetailsServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var person = personRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = person.getSystemRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getPermissionLevel()))
                .toList();

        return User.builder()
                .username(person.getLogin())
                .password(person.getPassword())
                .authorities(authorities)
                .build();
    }
}
