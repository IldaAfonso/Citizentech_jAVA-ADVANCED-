package br.fiap.citizentech.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.fiap.citizentech.dto.EventoRequest;
import br.fiap.citizentech.dto.EventoResponse;
import br.fiap.citizentech.model.StatusEvento;
import br.fiap.citizentech.model.TipoEvento;
import br.fiap.citizentech.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Alertas enviados pelo dispositivo (botão, remédio, sem movimento).")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar eventos com filtros e paginação",
            description = "Filtros opcionais: idosoId, tipo, status, de, ate (ISO, ex.: 2026-11-19T08:00:00). Ordenação: ?sort=dataHora,desc")
    @GetMapping
    public PagedModel<EntityModel<EventoResponse>> listar(
            @RequestParam(required = false) Long idosoId,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) StatusEvento status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate,
            @ParameterObject @PageableDefault(size = 10, sort = "dataHora", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return Paginas.modelo(service.listar(idosoId, tipo, status, de, ate, pageable), this::comLinks);
    }

    @Operation(summary = "Buscar evento por id")
    @GetMapping("/{id}")
    public EntityModel<EventoResponse> buscar(@PathVariable Long id) {
        return comLinks(service.buscar(id));
    }

    @Operation(summary = "Registrar evento", description = "Chamado pelo gateway do dispositivo. Status padrão: PENDENTE.")
    @PostMapping
    public ResponseEntity<EntityModel<EventoResponse>> criar(@Valid @RequestBody EventoRequest r) {
        EventoResponse criado = service.criar(r);
        return ResponseEntity.created(linkTo(methodOn(EventoController.class).buscar(criado.id())).toUri())
                .body(comLinks(criado));
    }

    @Operation(summary = "Atualizar evento")
    @PutMapping("/{id}")
    public EntityModel<EventoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EventoRequest r) {
        return comLinks(service.atualizar(id, r));
    }

    @Operation(summary = "Marcar evento como resolvido", description = "O cuidador confirma que tratou o alerta.")
    @PatchMapping("/{id}/resolver")
    public EntityModel<EventoResponse> resolver(@PathVariable Long id) {
        return comLinks(service.resolver(id));
    }

    @Operation(summary = "Excluir evento (somente ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<EventoResponse> comLinks(EventoResponse e) {
        EntityModel<EventoResponse> m = EntityModel.of(e,
                linkTo(methodOn(EventoController.class).buscar(e.id())).withSelfRel(),
                linkTo(methodOn(IdosoController.class).buscar(e.idosoId())).withRel("idoso"));
        if (e.status() != StatusEvento.RESOLVIDO) {
            m.add(Link.of("/api/eventos/" + e.id() + "/resolver", "resolver"));
        }
        return m;
    }
}
