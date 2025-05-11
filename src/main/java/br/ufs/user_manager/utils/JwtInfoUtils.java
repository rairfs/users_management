package br.ufs.user_manager.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.Optional;

public class JwtInfoUtils {

    public static String getUsernameFromSecurityContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return null;
    }

    public static Optional<String> getClaim(String claimName) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            Map<String, Object> claims = jwt.getClaims();

            String claim = String.valueOf(claims.get("scope"));
            if (claim.contains(claimName)) {
                return Optional.of(claim);
            }
        }
        return Optional.empty();
    }
}
