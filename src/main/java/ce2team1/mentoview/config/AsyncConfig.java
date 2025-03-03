package ce2team1.mentoview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "generateFeedbackExecutor")
    public Executor generateFeedbackExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);   // 최소 스레드 개수 (기본값)
        executor.setMaxPoolSize(50);   // 최대 스레드 개수 (테스트 후 조정 가능)
        executor.setQueueCapacity(200); // 대기 큐 크기 (이후 요청을 저장)
        executor.setThreadNamePrefix("Feedback-");
        executor.initialize();
        return executor;
    }
}
