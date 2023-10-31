package cz.upce.gremis.gremisng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executors;

@Configuration
class ThreadConfig implements AsyncConfigurer {


    @Bean
    @Primary
    public AsyncTaskExecutor asyncTaskExecutor() {
        var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setQueueCapacity(50);
        threadPoolTaskExecutor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor);
    }

    @Bean
    public AsyncTaskExecutor readOnlyOpsTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

   /* @Bean
    public DelegatingSecurityContextAsyncTaskExecutor writeOperationTaskExecutor() {
        var threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setQueueCapacity(50);
        threadPoolTaskExecutor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor);
    }*/

    /*@Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }*/
}
