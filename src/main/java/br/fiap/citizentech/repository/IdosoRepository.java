package br.fiap.citizentech.repository;

import br.fiap.citizentech.model.Idoso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdosoRepository extends JpaRepository<Idoso, Long> {

    // Query method derivada: o Spring monta o SQL a partir do nome do método
    Optional<Idoso> findByCodigoDispositivo(String codigoDispositivo);

    List<Idoso> findByCuidadorIdOrderByNomeAsc(Long cuidadorId);

    boolean existsByCodigoDispositivo(String codigoDispositivo);
}
