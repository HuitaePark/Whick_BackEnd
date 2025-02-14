package com.dasom.whick.domain.Collision;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollisionRepository extends JpaRepository<Collision, Long> {
}