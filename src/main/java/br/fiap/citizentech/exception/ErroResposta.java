package br.fiap.citizentech.exception;

import java.time.Instant;
import java.util.List;

// Formato padrão de todas as respostas de erro da API
public record ErroResposta(Instant timestamp, int status, String erro, String mensagem, List<String> detalhes) { }
