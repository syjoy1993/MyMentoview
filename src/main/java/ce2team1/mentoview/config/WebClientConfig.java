package ce2team1.mentoview.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    /*
     * todo
     *  - 백프레셔 V
     *  - timeout 설정 필요 V
     *  - timeout -> 시간에대해서 검색 후 문서화 할 것
     * */

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3)) // 응답 타임아웃 설정
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000) // 연결 타임아웃 설정
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS)) // 읽기 타임아웃 설정
                                .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))); // 쓰기 타임아웃 설정

        return builder.clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
