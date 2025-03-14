package ce2team1.mentoview.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class LambdaRequestFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private static final String ENDPOINT = "/api/interview/response/transcription";

    public LambdaRequestFilter() {
        this.requestMatcher = new AntPathRequestMatcher(ENDPOINT);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 요청이 특정 API에 해당하지 않으면 필터를 건너뜀
        if (!requestMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }

        // AWS Lambda 요청을 식별하는 헤더 값 확인
        String lambdaApiKey = request.getHeader("X-LAMBDA-API");
        if (!"test".equals(lambdaApiKey)) {
            log.info("============== Wrong Header Value: {} ================", lambdaApiKey);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized Lambda request.");
            return;
        }

        chain.doFilter(request, response);
    }
}
