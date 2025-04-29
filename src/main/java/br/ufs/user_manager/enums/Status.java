package br.ufs.user_manager.enums;

public enum Status {
    ACTIVE(1),
    INACTIVE(0);

    public final Integer value;

    Status(Integer value) {
        this.value = value;
    }
}
