package br.fiap.citizentech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "O usuário é obrigatório.") @Size(min = 3, max = 40, message = "Usuário com 3 a 40 caracteres.") String username,
        @NotBlank(message = "A senha é obrigatória.") @Size(min = 6, max = 60, message = "Senha com 6 a 60 caracteres.") String senha) { }
