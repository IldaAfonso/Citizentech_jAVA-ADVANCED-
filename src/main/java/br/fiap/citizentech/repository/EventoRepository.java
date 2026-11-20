package br.fiap.citizentech.repository;

import br.fiap.citizentech.model.Evento;
import br.fiap.citizentech.model.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// JpaSpecificationExecutor habilita a busca com filtros dinâmicos (EventoSpecs)
public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    // JPQL: conta os eventos de um status em todos os idosos de um cuidador (percorre Evento > Idoso > Cuidador)
    @Query("select count(e) from Evento e where e.idoso.cuidador.id = :cuidadorId and e.status = :status")
    long contarPorCuidadorEStatus(@Param("cuidadorId") Long cuidadorId, @Param("status") StatusEvento status);
}
