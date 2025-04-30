package br.ufs.user_manager.enums;

public enum Status {

    INACTIVE(0),
    ACTIVE(1);

    public final Integer value;

    Status(Integer value) {
        this.value = value;
    }
}
