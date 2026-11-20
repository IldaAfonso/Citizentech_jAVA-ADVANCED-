package br.fiap.citizentech.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CuidadorRequest(
        @NotBlank(message = "O nome é obrigatório.") @Size(max = 100, message = "Nome com no máximo 100 caracteres.") String nome,
        @NotBlank(message = "O telefone é obrigatório.") @Size(max = 20, message = "Telefone com no máximo 20 caracteres.") String telefone,
        @Email(message = "E-mail inválido.") @Size(max = 120) String email) { }
