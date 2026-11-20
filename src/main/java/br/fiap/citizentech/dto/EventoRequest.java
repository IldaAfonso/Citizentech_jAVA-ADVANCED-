package br.fiap.citizentech.dto;

import br.fiap.citizentech.model.StatusEvento;
import br.fiap.citizentech.model.TipoEvento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EventoRequest(
        @NotNull(message = "idosoId é obrigatório.") Long idosoId,
        @NotNull(message = "O tipo é obrigatório (BOTAO_EMERGENCIA, MEDICAMENTO_TOMADO, MEDICAMENTO_NAO_TOMADO ou SEM_MOVIMENTO).") TipoEvento tipo,
        StatusEvento status,
        @Size(max = 200, message = "Observação com no máximo 200 caracteres.") String observacao) { }
