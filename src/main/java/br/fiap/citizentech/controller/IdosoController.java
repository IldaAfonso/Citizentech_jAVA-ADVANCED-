package br.fiap.citizentech.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.fiap.citizentech.dto.IdosoRequest;
import br.fiap.citizentech.dto.IdosoResponse;
import br.fiap.citizentech.service.IdosoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/idosos")
@Tag(name = "Idosos", description = "Pessoas monitoradas pelo dispositivo em casa.")
public class IdosoController {

    private final IdosoService service;

    public IdosoController(IdosoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar idosos (paginado)")
    @GetMapping
    public PagedModel<EntityModel<IdosoResponse>> listar(
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return Paginas.modelo(service.listar(pageable), this::comLinks);
    }

    @Operation(summary = "Buscar idoso por id")
    @GetMapping("/{id}")
    public EntityModel<IdosoResponse> buscar(@PathVariable Long id) {
        return comLinks(service.buscar(id));
    }

    @Operation(summary = "Cadastrar idoso")
    @PostMapping
    public ResponseEntity<EntityModel<IdosoResponse>> criar(@Valid @RequestBody IdosoRequest r) {
        IdosoResponse criado = service.criar(r);
        return ResponseEntity.created(linkTo(methodOn(IdosoController.class).buscar(criado.id())).toUri())
                .body(comLinks(criado));
    }

    @Operation(summary = "Atualizar idoso")
    @PutMapping("/{id}")
    public EntityModel<IdosoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody IdosoRequest r) {
        return comLinks(service.atualizar(id, r));
    }

    @Operation(summary = "Excluir idoso (somente ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<IdosoResponse> comLinks(IdosoResponse i) {
        return EntityModel.of(i,
                linkTo(methodOn(IdosoController.class).buscar(i.id())).withSelfRel(),
                linkTo(methodOn(CuidadorController.class).buscar(i.cuidadorId())).withRel("cuidador"),
                Link.of("/api/eventos?idosoId=" + i.id(), "eventos"));
    }
}
