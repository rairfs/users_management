package br.ufs.user_manager.enums;

public enum RoleType {
    ADMIN(1L),
    BASIC(2L);

    long roleId;

    RoleType(long roleId) {
        this.roleId = roleId;
    }
}
