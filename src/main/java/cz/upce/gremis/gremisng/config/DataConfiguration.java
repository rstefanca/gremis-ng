package cz.upce.gremis.gremisng.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
class DataConfiguration {

   /* @Bean
    AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }*/
}