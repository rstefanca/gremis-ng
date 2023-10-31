package cz.upce.gremis.gremisng.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
class DataConfiguration {

    @Bean
    AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }

    private static class AuditorAwareImpl implements AuditorAware<Long> {

        @Override
        public @NonNull Optional<Long> getCurrentAuditor() {
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                    .map(this::getPersonId);
        }

        private Long getPersonId(Authentication authentication) {
            var principal = authentication.getPrincipal();
            if (principal != null) {
                return 1L;
            }
            return null;


            /*if (principal instanceof GremisUser) {
                return ((GremisUser) principal).getPersonId().getValue();
            }

            throw new IllegalStateException("Unknown principal object type");*/
        }
    }
}