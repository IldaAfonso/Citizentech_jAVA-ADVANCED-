package br.fiap.citizentech.service;

import br.fiap.citizentech.dto.EventoRequest;
import br.fiap.citizentech.dto.EventoResponse;
import br.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.fiap.citizentech.model.Evento;
import br.fiap.citizentech.model.Idoso;
import br.fiap.citizentech.model.StatusEvento;
import br.fiap.citizentech.model.TipoEvento;
import br.fiap.citizentech.repository.EventoRepository;
import br.fiap.citizentech.repository.EventoSpecs;
import br.fiap.citizentech.repository.IdosoRepository;
import java.time.LocalDateTime;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoService {

    private final EventoRepository repo;
    private final IdosoRepository idosoRepo;

    public EventoService(EventoRepository repo, IdosoRepository idosoRepo) {
        this.repo = repo;
        this.idosoRepo = idosoRepo;
    }

    @Transactional(readOnly = true)
    public Page<EventoResponse> listar(Long idosoId, TipoEvento tipo, StatusEvento status,
                                       LocalDateTime de, LocalDateTime ate, Pageable pageable) {
        return repo.findAll(EventoSpecs.filtrar(idosoId, tipo, status, de, ate), pageable)
                .map(EventoService::paraResposta);
    }

    @Transactional(readOnly = true)
    public EventoResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    @Transactional
    @CacheEvict(value = "resumoCuidador", allEntries = true)
    public EventoResponse criar(EventoRequest r) {
        Idoso idoso = idosoRepo.findById(r.idosoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso não encontrado: " + r.idosoId()));
        Evento e = new Evento();
        e.setIdoso(idoso);
        e.setTipo(r.tipo());
        e.setStatus(r.status() != null ? r.status() : StatusEvento.PENDENTE);
        e.setDataHora(LocalDateTime.now());
        e.setObservacao(r.observacao());
        return paraResposta(repo.save(e));
    }

    @Transactional
    @CacheEvict(value = "resumoCuidador", allEntries = true)
    public EventoResponse atualizar(Long id, EventoRequest r) {
        Evento e = obter(id);
        Idoso idoso = idosoRepo.findById(r.idosoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso não encontrado: " + r.idosoId()));
        e.setIdoso(idoso);
        e.setTipo(r.tipo());
        if (r.status() != null) e.setStatus(r.status());
        e.setObservacao(r.observacao());
        return paraResposta(e);
    }

    @Transactional
    @CacheEvict(value = "resumoCuidador", allEntries = true)
    public EventoResponse resolver(Long id) {
        Evento e = obter(id);
        e.setStatus(StatusEvento.RESOLVIDO);
        return paraResposta(e);
    }

    @Transactional
    @CacheEvict(value = "resumoCuidador", allEntries = true)
    public void excluir(Long id) {
        repo.delete(obter(id));
    }

    Evento obter(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado: " + id));
    }

    // Regra de negócio: a gravidade vem do tipo do evento
    static String gravidade(TipoEvento tipo) {
        return switch (tipo) {
            case BOTAO_EMERGENCIA -> "CRITICA";
            case SEM_MOVIMENTO -> "ALTA";
            case MEDICAMENTO_NAO_TOMADO -> "MEDIA";
            case MEDICAMENTO_TOMADO -> "BAIXA";
        };
    }

    static EventoResponse paraResposta(Evento e) {
        return new EventoResponse(e.getId(), e.getIdoso().getId(), e.getTipo(), e.getStatus(),
                e.getDataHora(), e.getObservacao(), gravidade(e.getTipo()));
    }
}
