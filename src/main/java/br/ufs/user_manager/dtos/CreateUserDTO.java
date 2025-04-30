package br.ufs.user_manager.dtos;

import java.util.List;

public record CreateUserDTO(
        String nome,
        String email,
        String senha,
        List<AddressDTO> enderecos
) {}
