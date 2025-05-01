package br.ufs.user_manager.dtos;

import java.util.List;

public record UserDTO(
        String nome,
        String email,
        List<AddressDTO> enderecos
) {}
