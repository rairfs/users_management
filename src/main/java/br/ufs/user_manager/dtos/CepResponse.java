package br.ufs.user_manager.dtos;

public record CepResponse(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf
) {}
