package br.ufs.user_manager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateUserDTO(

        @NotBlank
        String name,

        @NotBlank
        @Email(message = "Please, insert a valid email address")
        String email,

        @NotBlank
        String password,
        List<AddressCreationDTO> addresses
) {}
