package br.ufs.user_manager.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record UserDTO(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<AddressResponseDTO> addresses
) {}
