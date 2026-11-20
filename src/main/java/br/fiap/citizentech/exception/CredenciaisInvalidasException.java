package br.fiap.citizentech.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() { super("Usuário ou senha inválidos."); }
}
