package br.fiap.citizentech.repository;

import br.fiap.citizentech.model.Evento;
import br.fiap.citizentech.model.StatusEvento;
import br.fiap.citizentech.model.TipoEvento;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

// Filtros dinâmicos: só entra na consulta o filtro que o usuário informou
public final class EventoSpecs {

    private EventoSpecs() { }

    public static Specification<Evento> filtrar(Long idosoId, TipoEvento tipo, StatusEvento status,
                                                LocalDateTime de, LocalDateTime ate) {
        return (root, query, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();
            if (idosoId != null) condicoes.add(cb.equal(root.get("idoso").get("id"), idosoId));
            if (tipo != null) condicoes.add(cb.equal(root.get("tipo"), tipo));
            if (status != null) condicoes.add(cb.equal(root.get("status"), status));
            if (de != null) condicoes.add(cb.greaterThanOrEqualTo(root.get("dataHora"), de));
            if (ate != null) condicoes.add(cb.lessThanOrEqualTo(root.get("dataHora"), ate));
            return cb.and(condicoes.toArray(new Predicate[0]));
        };
    }
}
