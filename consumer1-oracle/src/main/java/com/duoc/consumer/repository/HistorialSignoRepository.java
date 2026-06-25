package com.duoc.consumer.repository;

import com.duoc.consumer.model.HistorialSigno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialSignoRepository extends JpaRepository<HistorialSigno, Long> {

    List<HistorialSigno> findByTipoOrderByFechaRecepcionDesc(String tipo);
}
