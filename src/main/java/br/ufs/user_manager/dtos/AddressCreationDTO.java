package br.ufs.user_manager.dtos;

public record AddressCreationDTO(
        String complement,
        String postalCode,
        String number
) {}
