package br.ufs.user_manager.dtos;

public record LoginResponse(
        String accessToken,
        Long expiresIn
) {}
