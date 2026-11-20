package br.fiap.citizentech.config;

import br.fiap.citizentech.model.*;
import br.fiap.citizentech.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Dados de demonstração (H2 em memória). Só roda se o banco estiver vazio.
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner carregar(UsuarioRepository usuarios, CuidadorRepository cuidadores, IdosoRepository idosos,
                               EventoRepository eventos, PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() > 0) return;
            usuarios.save(new Usuario("admin", encoder.encode("admin123"), Papel.ADMIN));
            usuarios.save(new Usuario("cuidador", encoder.encode("cuidador123"), Papel.CUIDADOR));

            Cuidador c1 = cuidador(cuidadores, "Mariana Souza", "11990000001", "mariana@email.com");
            Cuidador c2 = cuidador(cuidadores, "Carlos Lima", "11990000002", "carlos@email.com");
            cuidador(cuidadores, "Beatriz Alves", "11990000003", "beatriz@email.com");

            Idoso i1 = idoso(idosos, c1, "Dona Helena", LocalDate.of(1944, 3, 12), "ESP-0001");
            Idoso i2 = idoso(idosos, c1, "Seu Antônio", LocalDate.of(1939, 8, 2), "ESP-0002");
            Idoso i3 = idoso(idosos, c2, "Dona Lourdes", LocalDate.of(1947, 11, 25), "ESP-0003");
            idoso(idosos, c2, "Seu Joaquim", LocalDate.of(1941, 5, 30), "ESP-0004");

            LocalDateTime agora = LocalDateTime.now();
            evento(eventos, i1, TipoEvento.BOTAO_EMERGENCIA, StatusEvento.PENDENTE, agora.minusMinutes(5), "Botão pressionado");
            evento(eventos, i1, TipoEvento.MEDICAMENTO_TOMADO, StatusEvento.RESOLVIDO, agora.minusHours(3), "Remédio das 8h");
            evento(eventos, i1, TipoEvento.MEDICAMENTO_NAO_TOMADO, StatusEvento.PENDENTE, agora.minusHours(1), "Remédio das 20h não tomado");
            evento(eventos, i2, TipoEvento.SEM_MOVIMENTO, StatusEvento.PENDENTE, agora.minusMinutes(30), "Sem movimento há 4h");
            evento(eventos, i2, TipoEvento.MEDICAMENTO_TOMADO, StatusEvento.RESOLVIDO, agora.minusHours(6), "Remédio da manhã");
            evento(eventos, i3, TipoEvento.BOTAO_EMERGENCIA, StatusEvento.RESOLVIDO, agora.minusDays(1), "Falsa ativação");
            evento(eventos, i3, TipoEvento.MEDICAMENTO_NAO_TOMADO, StatusEvento.PENDENTE, agora.minusMinutes(50), "Caixa de remédios fechada");
            evento(eventos, i3, TipoEvento.SEM_MOVIMENTO, StatusEvento.RESOLVIDO, agora.minusDays(2), "Dormiu no sofá");
        };
    }

    private static Cuidador cuidador(CuidadorRepository repo, String nome, String tel, String email) {
        Cuidador c = new Cuidador();
        c.setNome(nome); c.setTelefone(tel); c.setEmail(email);
        return repo.save(c);
    }

    private static Idoso idoso(IdosoRepository repo, Cuidador c, String nome, LocalDate nasc, String codigo) {
        Idoso i = new Idoso();
        i.setCuidador(c); i.setNome(nome); i.setDataNascimento(nasc); i.setCodigoDispositivo(codigo);
        return repo.save(i);
    }

    private static void evento(EventoRepository repo, Idoso i, TipoEvento t, StatusEvento s, LocalDateTime quando, String obs) {
        Evento e = new Evento();
        e.setIdoso(i); e.setTipo(t); e.setStatus(s); e.setDataHora(quando); e.setObservacao(obs);
        repo.save(e);
    }
}
