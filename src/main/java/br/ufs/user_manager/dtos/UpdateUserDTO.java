package br.ufs.user_manager.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateUserDTO(

        String name,

        @Email(message = "Please, insert a valid email address")
        String email,

        String password,
        List<AddressCreationDTO> addresses
) {}
