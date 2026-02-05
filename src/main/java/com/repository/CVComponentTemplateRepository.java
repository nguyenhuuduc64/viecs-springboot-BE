package com.repository;

import com.entity.CVComponentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CVComponentTemplateRepository extends JpaRepository<CVComponentTemplate, String> {
}
