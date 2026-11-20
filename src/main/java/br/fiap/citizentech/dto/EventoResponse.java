package br.fiap.citizentech.dto;

import br.fiap.citizentech.model.StatusEvento;
import br.fiap.citizentech.model.TipoEvento;
import java.time.LocalDateTime;

public record EventoResponse(Long id, Long idosoId, TipoEvento tipo, StatusEvento status,
                             LocalDateTime dataHora, String observacao, String gravidade) { }
