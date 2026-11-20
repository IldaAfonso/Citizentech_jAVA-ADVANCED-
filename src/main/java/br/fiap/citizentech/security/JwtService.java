package br.fiap.citizentech.security;

import br.fiap.citizentech.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${citizentech.jwt.secret}") String segredo,
                      @Value("${citizentech.jwt.expiracao-minutos}") long minutos) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = minutos * 60_000L;
    }

    public String gerar(Usuario usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("papel", usuario.getPapel().name())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    // Lança JwtException se o token for inválido, adulterado ou expirado
    public Claims ler(String token) {
        return Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload();
    }
}
