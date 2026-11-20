package br.fiap.citizentech.controller;

import br.fiap.citizentech.dto.LoginRequest;
import br.fiap.citizentech.dto.RegistroRequest;
import br.fiap.citizentech.dto.TokenResponse;
import br.fiap.citizentech.exception.CredenciaisInvalidasException;
import br.fiap.citizentech.exception.RegraNegocioException;
import br.fiap.citizentech.model.Papel;
import br.fiap.citizentech.model.Usuario;
import br.fiap.citizentech.repository.UsuarioRepository;
import br.fiap.citizentech.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Login e cadastro. Retorna o token JWT usado nos demais endpoints.")
public class AuthController {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Operation(summary = "Login", description = "Valida usuário e senha e devolve um token JWT (Bearer).")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest r) {
        Usuario u = repo.findByUsername(r.username())
                .filter(x -> encoder.matches(r.senha(), x.getSenha()))
                .orElseThrow(CredenciaisInvalidasException::new);
        return new TokenResponse(jwt.gerar(u), "Bearer", u.getPapel().name());
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria um usuário com o papel CUIDADOR (a senha é guardada com BCrypt).")
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registrar(@Valid @RequestBody RegistroRequest r) {
        if (repo.existsByUsername(r.username())) {
            throw new RegraNegocioException("Usuário já existe: " + r.username());
        }
        Usuario u = repo.save(new Usuario(r.username(), encoder.encode(r.senha()), Papel.CUIDADOR));
        return new TokenResponse(jwt.gerar(u), "Bearer", u.getPapel().name());
    }
}
