package cloud.chlora.shared.util;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public final class CookieAuth {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";

    private CookieAuth() {}

    public static String accessTokenCookie(String token) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(8))
                .build()
                .toString();
    }

    public static String clearAccessTokenCookie() {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ZERO)
                .build()
                .toString();
    }
}