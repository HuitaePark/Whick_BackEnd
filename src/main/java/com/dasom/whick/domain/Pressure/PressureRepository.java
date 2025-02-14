package com.dasom.whick.domain.Pressure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PressureRepository extends JpaRepository<Pressure, Long> {
}