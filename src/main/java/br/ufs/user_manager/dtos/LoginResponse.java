package br.ufs.user_manager.dtos;

public record LoginResponse(
        String acessToken,
        Long expiresIn
) {}
