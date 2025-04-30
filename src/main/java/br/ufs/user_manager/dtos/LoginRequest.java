package br.ufs.user_manager.dtos;

public record LoginRequest(
        String email,
        String senha
) {}
