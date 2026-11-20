package br.fiap.citizentech.service;

import br.fiap.citizentech.dto.CuidadorRequest;
import br.fiap.citizentech.dto.CuidadorResponse;
import br.fiap.citizentech.dto.ResumoCuidadorResponse;
import br.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.fiap.citizentech.model.Cuidador;
import br.fiap.citizentech.model.StatusEvento;
import br.fiap.citizentech.repository.CuidadorRepository;
import br.fiap.citizentech.repository.EventoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuidadorService {

    private final CuidadorRepository repo;
    private final EventoRepository eventoRepo;

    public CuidadorService(CuidadorRepository repo, EventoRepository eventoRepo) {
        this.repo = repo;
        this.eventoRepo = eventoRepo;
    }

    @Transactional(readOnly = true)
    public Page<CuidadorResponse> listar(Pageable pageable) {
        return repo.findAll(pageable).map(CuidadorService::paraResposta);
    }

    @Transactional(readOnly = true)
    public CuidadorResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    // Cache: o resumo (JPQL com join) é consultado toda hora pelo app; só muda quando há escrita
    @Cacheable(value = "resumoCuidador", key = "#id")
    @Transactional(readOnly = true)
    public ResumoCuidadorResponse resumo(Long id) {
        Cuidador c = obter(id);
        long pendentes = eventoRepo.contarPorCuidadorEStatus(id, StatusEvento.PENDENTE);
        return new ResumoCuidadorResponse(c.getId(), c.getNome(), pendentes);
    }

    @Transactional
    @CacheEvict(value = {"resumoCuidador", "idososDoCuidador"}, allEntries = true)
    public CuidadorResponse criar(CuidadorRequest r) {
        Cuidador c = new Cuidador();
        aplicar(c, r);
        return paraResposta(repo.save(c));
    }

    @Transactional
    @CacheEvict(value = {"resumoCuidador", "idososDoCuidador"}, allEntries = true)
    public CuidadorResponse atualizar(Long id, CuidadorRequest r) {
        Cuidador c = obter(id);
        aplicar(c, r);
        return paraResposta(c);
    }

    @Transactional
    @CacheEvict(value = {"resumoCuidador", "idososDoCuidador"}, allEntries = true)
    public void excluir(Long id) {
        repo.delete(obter(id));
    }

    Cuidador obter(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cuidador não encontrado: " + id));
    }

    private static void aplicar(Cuidador c, CuidadorRequest r) {
        c.setNome(r.nome());
        c.setTelefone(r.telefone());
        c.setEmail(r.email());
    }

    static CuidadorResponse paraResposta(Cuidador c) {
        return new CuidadorResponse(c.getId(), c.getNome(), c.getTelefone(), c.getEmail());
    }
}
