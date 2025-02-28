package ce2team1.mentoview.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AI 면접 서비스 API",
                version = "1.0",
                description = "AI 면접 서비스의 API 문서"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버"),
                @Server(url = "https://mentoview.site", description = "배포 서버")
        }
)
public class SwaggerConfig {
}
