package ce2team1.mentoview.security;

import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.security.dto.MvPrincipalDetails;
import ce2team1.mentoview.service.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

/*
* 클라이언트 검증
* */
@RequiredArgsConstructor
public class MvRequestFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = request.getHeader("Authorization");
        if (authToken == null) {
            filterChain.doFilter(request, response);
            return;
        }


        if (jwtTokenProvider.isExpired(authToken)) {
            String emailFromToken = jwtTokenProvider.getEmailFromToken(authToken);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter writer = response.getWriter();
            writer.write("{\"error\":\"Expired JWT token\"}");
            writer.flush();
            return;

        }

        String type = jwtTokenProvider.getType(authToken);
        if (!type.equals("access")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter writer = response.getWriter();
            writer.write("{\"error\":\"invalid JWT token\"}");
            writer.flush();
            return;
        }

        String emailFromToken = jwtTokenProvider.getEmailFromToken(authToken);
        Role roleFromToken = jwtTokenProvider.getRoleFromToken(authToken);


        MvPrincipalDetails mvPrincipalDetails = new MvPrincipalDetails(UserDto.of(emailFromToken, roleFromToken));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mvPrincipalDetails,null, mvPrincipalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
