package ce2team1.mentoview.config;

import ce2team1.mentoview.security.MvOAuth2AuthenticationFailureHandler;
import ce2team1.mentoview.security.MvOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final MvOAuth2UserService mvOAuth2UserService;
    private final MvOAuth2AuthenticationFailureHandler mvOAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                //.oauth2Login((Customizer.withDefaults()))//oauth2
                .formLogin(formLogin -> formLogin.loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password"))

                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts.disable()));//AWS
        //OAuth2
        security.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(mvOAuth2UserService))
                .failureHandler(mvOAuth2AuthenticationFailureHandler));
        // 인가
        security.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/api/test").permitAll()
                        .requestMatchers("/", "/favicon.ico", "/static","/about","/contactus").permitAll()
                        .requestMatchers("/api/signup/**", "/api/login/**").permitAll()
                        .requestMatchers("/error", "/error/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );
        //세션 비활
        security.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return security.build();

    }
}
