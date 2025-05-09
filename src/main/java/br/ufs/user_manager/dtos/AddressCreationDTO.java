package br.ufs.user_manager.dtos;

import jakarta.validation.constraints.NotBlank;

public record AddressCreationDTO(
        String complement,

        @NotBlank
        String postalCode,

        String number
) {}
