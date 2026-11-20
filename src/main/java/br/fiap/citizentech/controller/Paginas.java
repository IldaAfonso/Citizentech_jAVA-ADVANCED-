package br.fiap.citizentech.controller;

import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

// Converte um Page em PagedModel (HATEOAS) com os metadados de paginação.
final class Paginas {

    private Paginas() { }

    static <T> PagedModel<EntityModel<T>> modelo(Page<T> pagina, Function<T, EntityModel<T>> conversor) {
        var itens = pagina.getContent().stream().map(conversor).toList();
        var meta = new PagedModel.PageMetadata(pagina.getSize(), pagina.getNumber(),
                pagina.getTotalElements(), pagina.getTotalPages());
        return PagedModel.of(itens, meta);
    }
}
