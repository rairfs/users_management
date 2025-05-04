package br.ufs.user_manager.dtos;

public record ErrorResponse(
        Integer status,
        String error,
        String message,
        String path
) {}
