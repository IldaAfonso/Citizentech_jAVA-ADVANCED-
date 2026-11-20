package br.fiap.citizentech.service;

import br.fiap.citizentech.dto.IdosoRequest;
import br.fiap.citizentech.dto.IdosoResponse;
import br.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.fiap.citizentech.exception.RegraNegocioException;
import br.fiap.citizentech.model.Cuidador;
import br.fiap.citizentech.model.Idoso;
import br.fiap.citizentech.repository.CuidadorRepository;
import br.fiap.citizentech.repository.IdosoRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdosoService {

    private final IdosoRepository repo;
    private final CuidadorRepository cuidadorRepo;

    public IdosoService(IdosoRepository repo, CuidadorRepository cuidadorRepo) {
        this.repo = repo;
        this.cuidadorRepo = cuidadorRepo;
    }

    @Transactional(readOnly = true)
    public Page<IdosoResponse> listar(Pageable pageable) {
        return repo.findAll(pageable).map(IdosoService::paraResposta);
    }

    @Transactional(readOnly = true)
    public IdosoResponse buscar(Long id) {
        return paraResposta(obter(id));
    }

    // Cache: lista dos idosos de um cuidador (tela principal do app)
    @Cacheable(value = "idososDoCuidador", key = "#cuidadorId")
    @Transactional(readOnly = true)
    public List<IdosoResponse> listarPorCuidador(Long cuidadorId) {
        if (!cuidadorRepo.existsById(cuidadorId)) {
            throw new RecursoNaoEncontradoException("Cuidador não encontrado: " + cuidadorId);
        }
        return repo.findByCuidadorIdOrderByNomeAsc(cuidadorId).stream().map(IdosoService::paraResposta).toList();
    }

    @Transactional
    @CacheEvict(value = {"resumoCuidador", "idososDoCuidador"}, allEntries = true)
    public IdosoResponse criar(IdosoRequest r) {
        if (repo.existsByCodigoDispositivo(r.codigoDispositivo())) {
            throw new RegraNegocioException("Já existe um idoso com o dispositivo " + r.codigoDispositivo());
        }
        Idoso i = new Idoso();
        aplicar(i, r);
        return paraResposta(repo.save(i));
    }

    @Transactional
    @CacheEvict(value = {"resumoCuidador", "idososDoCuidador"}, allEntries = true)
    public IdosoResponse atualizar(Long id, IdosoRequest r) {
        Idoso i = obter(id);
        repo.findByCodigoDispositivo(r.codigoDispositivo()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new RegraNegocioException("Já existe um idoso com o dispositivo " + r.codigoDispositivo());
            }
        });
        aplicar(i, r);
        return paraResposta(i);
    }

    @Transactional
    @CacheEvict(value = {"resumoCuidador", "idososDoCuidador"}, allEntries = true)
    public void excluir(Long id) {
        repo.delete(obter(id));
    }

    Idoso obter(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso não encontrado: " + id));
    }

    private void aplicar(Idoso i, IdosoRequest r) {
        Cuidador c = cuidadorRepo.findById(r.cuidadorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cuidador não encontrado: " + r.cuidadorId()));
        i.setCuidador(c);
        i.setNome(r.nome());
        i.setDataNascimento(r.dataNascimento());
        i.setCodigoDispositivo(r.codigoDispositivo());
    }

    static IdosoResponse paraResposta(Idoso i) {
        return new IdosoResponse(i.getId(), i.getCuidador().getId(), i.getNome(), i.getDataNascimento(), i.getCodigoDispositivo());
    }
}
