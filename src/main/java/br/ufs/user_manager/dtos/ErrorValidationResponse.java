package br.ufs.user_manager.dtos;

import java.util.Map;

public record ErrorValidationResponse(
        Integer status,
        String message,
        String path,
        Map<String, String> errors
) {}
