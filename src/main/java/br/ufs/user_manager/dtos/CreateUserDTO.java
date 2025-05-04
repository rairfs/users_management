package br.ufs.user_manager.dtos;

import java.util.List;

public record CreateUserDTO(
        String name,
        String email,
        String password,
        List<AddressCreationDTO> addresses
) {}
