package com.carlosdaza.financetracker.repository;

import com.carlosdaza.financetracker.domain.entity.Transaction;
import com.carlosdaza.financetracker.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // ── By day ────────────────────────────────────────────────────────────────
    List<Transaction> findByTransactionDateOrderByCreatedAtDesc(LocalDate date);

    // ── By month ──────────────────────────────────────────────────────────────
    @Query("""
        SELECT t FROM Transaction t
        WHERE YEAR(t.transactionDate) = :year
          AND MONTH(t.transactionDate) = :month
        ORDER BY t.transactionDate DESC, t.createdAt DESC
        """)
    List<Transaction> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // ── By year ───────────────────────────────────────────────────────────────
    @Query("""
        SELECT t FROM Transaction t
        WHERE YEAR(t.transactionDate) = :year
        ORDER BY t.transactionDate DESC, t.createdAt DESC
        """)
    List<Transaction> findByYear(@Param("year") int year);

    // ── Totals ────────────────────────────────────────────────────────────────
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.type = :type
          AND t.transactionDate BETWEEN :from AND :to
        """)
    BigDecimal sumByTypeAndDateBetween(
            @Param("type") TransactionType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // ── By category in period ─────────────────────────────────────────────────
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.category.id = :categoryId
          AND t.transactionDate BETWEEN :from AND :to
        ORDER BY t.transactionDate DESC
        """)
    List<Transaction> findByCategoryAndPeriod(
            @Param("categoryId") UUID categoryId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    // ── Summary grouped by category ───────────────────────────────────────────
    @Query("""
        SELECT t.category.id, t.category.name, t.type, SUM(t.amount), COUNT(t)
        FROM Transaction t
        WHERE t.transactionDate BETWEEN :from AND :to
        GROUP BY t.category.id, t.category.name, t.type
        ORDER BY SUM(t.amount) DESC
        """)
    List<Object[]> summarizeByCategory(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
