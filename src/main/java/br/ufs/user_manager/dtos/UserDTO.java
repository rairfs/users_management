package br.ufs.user_manager.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record UserDTO(
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AddressResponseDTO> addresses
) {}
