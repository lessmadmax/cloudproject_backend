package com.cloudproject.community_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.cloudproject.community_backend.entity.School;


public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);
}
