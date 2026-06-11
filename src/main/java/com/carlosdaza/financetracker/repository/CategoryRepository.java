package com.carlosdaza.financetracker.repository;

import com.carlosdaza.financetracker.domain.entity.Category;
import com.carlosdaza.financetracker.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    List<Category> findByType(TransactionType type);
}
