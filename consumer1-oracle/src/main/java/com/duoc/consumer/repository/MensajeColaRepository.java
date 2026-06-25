package com.duoc.consumer.repository;

import com.duoc.consumer.model.MensajeCola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeColaRepository extends JpaRepository<MensajeCola, Long> {
}
