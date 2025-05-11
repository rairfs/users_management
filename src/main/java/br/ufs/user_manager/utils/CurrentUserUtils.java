package br.ufs.user_manager.utils;

import java.util.Optional;

public class CurrentUserUtils {

    public static Long getCurrentUserID() {
        String userId = JwtInfoUtils.getUsernameFromSecurityContext();
        return convertId(userId);
    }

    private static Long convertId(String id) {
        if (id == null || id.isEmpty()) {
            throw new RuntimeException("ID não pode ser nulo");
        }

        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new RuntimeException("O formato do ID é inválido!");
        }
    }

    public static Boolean isCurrentUserAdmin() {
        Optional<String> claim = JwtInfoUtils.getClaim("ADMIN");

        return claim.isPresent();
    }
}
