package br.fiap.citizentech.dto;

import java.time.LocalDate;

public record IdosoResponse(Long id, Long cuidadorId, String nome, LocalDate dataNascimento, String codigoDispositivo) { }
