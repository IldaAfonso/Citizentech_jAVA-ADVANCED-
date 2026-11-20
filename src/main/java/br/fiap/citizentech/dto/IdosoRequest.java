package br.fiap.citizentech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record IdosoRequest(
        @NotNull(message = "cuidadorId é obrigatório.") Long cuidadorId,
        @NotBlank(message = "O nome é obrigatório.") @Size(max = 100) String nome,
        @NotNull(message = "A data de nascimento é obrigatória.") @Past(message = "A data de nascimento deve estar no passado.") LocalDate dataNascimento,
        @NotBlank(message = "O código do dispositivo é obrigatório.") @Size(max = 30) String codigoDispositivo) { }
