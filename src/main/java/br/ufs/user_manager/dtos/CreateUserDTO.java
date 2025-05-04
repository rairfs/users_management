package br.ufs.user_manager.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateUserDTO(

        @NotBlank
        String name,

        @NotBlank
        String email,

        @NotBlank
        String password,
        List<AddressCreationDTO> addresses
) {}
