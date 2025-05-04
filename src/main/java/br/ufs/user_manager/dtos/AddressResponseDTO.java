package br.ufs.user_manager.dtos;

public record AddressResponseDTO(
        String streetName,
        String number,
        String complement,
        String district,
        String city,
        String state,
        String postalCode
) {
}
