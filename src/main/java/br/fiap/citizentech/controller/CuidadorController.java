package br.fiap.citizentech.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.fiap.citizentech.dto.CuidadorRequest;
import br.fiap.citizentech.dto.CuidadorResponse;
import br.fiap.citizentech.dto.IdosoResponse;
import br.fiap.citizentech.dto.ResumoCuidadorResponse;
import br.fiap.citizentech.service.CuidadorService;
import br.fiap.citizentech.service.IdosoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuidadores")
@Tag(name = "Cuidadores", description = "Familiares/cuidadores que recebem os alertas.")
public class CuidadorController {

    private final CuidadorService service;
    private final IdosoService idosoService;

    public CuidadorController(CuidadorService service, IdosoService idosoService) {
        this.service = service;
        this.idosoService = idosoService;
    }

    @Operation(summary = "Listar cuidadores (paginado)")
    @GetMapping
    public PagedModel<EntityModel<CuidadorResponse>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return Paginas.modelo(service.listar(pageable), this::comLinks);
    }

    @Operation(summary = "Buscar cuidador por id")
    @GetMapping("/{id}")
    public EntityModel<CuidadorResponse> buscar(@PathVariable Long id) {
        return comLinks(service.buscar(id));
    }

    @Operation(summary = "Listar idosos do cuidador", description = "Resultado em cache (Spring Cache).")
    @GetMapping("/{id}/idosos")
    public List<IdosoResponse> idosos(@PathVariable Long id) {
        return idosoService.listarPorCuidador(id);
    }

    @Operation(summary = "Resumo do cuidador", description = "Quantidade de eventos pendentes (JPQL com join). Em cache.")
    @GetMapping("/{id}/resumo")
    public ResumoCuidadorResponse resumo(@PathVariable Long id) {
        return service.resumo(id);
    }

    @Operation(summary = "Criar cuidador")
    @PostMapping
    public ResponseEntity<EntityModel<CuidadorResponse>> criar(@Valid @RequestBody CuidadorRequest r) {
        CuidadorResponse criado = service.criar(r);
        var modelo = comLinks(criado);
        return ResponseEntity.created(linkTo(methodOn(CuidadorController.class).buscar(criado.id())).toUri()).body(modelo);
    }

    @Operation(summary = "Atualizar cuidador")
    @PutMapping("/{id}")
    public EntityModel<CuidadorResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CuidadorRequest r) {
        return comLinks(service.atualizar(id, r));
    }

    @Operation(summary = "Excluir cuidador (somente ADMIN)", description = "Remove também seus idosos e eventos (cascade).")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<CuidadorResponse> comLinks(CuidadorResponse c) {
        return EntityModel.of(c,
                linkTo(methodOn(CuidadorController.class).buscar(c.id())).withSelfRel(),
                linkTo(methodOn(CuidadorController.class).idosos(c.id())).withRel("idosos"),
                linkTo(methodOn(CuidadorController.class).resumo(c.id())).withRel("resumo"));
    }
}
