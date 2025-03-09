package ce2team1.mentoview.utils.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public class MvCookieUtils {
    //쿠키줘라
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("cookies is null: {}", name);
            return Optional.empty();
        }

        Optional<Cookie> firstCookie = Arrays.stream(cookies).filter(cookie ->
                cookie.getName().equals(name)).findFirst();
        if (firstCookie.isEmpty()) {
            log.info("cookies is null: {}", name);

        }
        return firstCookie;


    }

    //쿠키담아줘라
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);// http

/*        if (!"localhost".equals((System.getenv("HOST")))) {
            cookie.setSecure(true);
        }*/
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    cookie.setValue("");
                    response.addCookie(cookie);
                }
            }
        }
    }
    // 쿠키 암호화
    public static String incodingCookie(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    //쿠키 복호화
    public static <T> T decodingCookie(Cookie cookie, Class<T> clazz) {
        return clazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}
