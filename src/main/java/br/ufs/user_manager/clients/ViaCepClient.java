package br.ufs.user_manager.clients;

import br.ufs.user_manager.dtos.CepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viacep", url = "https://viacep.com.br/ws/")
public interface ViaCepClient {

    @GetMapping("/{cep}/json/")
    CepResponse getCep(@PathVariable("cep") String cep);
}
