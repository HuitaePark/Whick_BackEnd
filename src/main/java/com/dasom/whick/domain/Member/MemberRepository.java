package com.dasom.whick.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsByUsername(String username);
    boolean existsByName(String name);
    boolean existsById(@NonNull Long id);
    Optional<Member> findByUsername(String username);
}
