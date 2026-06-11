package com.carlosdaza.financetracker.domain.dto;

import com.carlosdaza.financetracker.domain.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FinanceDto {

    // ── Category ─────────────────────────────────────────────────────────────

    @Builder
    public record CategoryRequest(
            @NotBlank(message = "name is required")
            @Size(max = 100)
            String name,

            String description,

            @NotNull(message = "type is required (INCOME or EXPENSE)")
            TransactionType type
    ) {}

    @Builder
    public record CategoryResponse(
            UUID id,
            String name,
            String description,
            TransactionType type,
            LocalDateTime createdAt
    ) {}

    // ── Transaction ───────────────────────────────────────────────────────────

    @Builder
    public record TransactionRequest(
            @NotNull(message = "categoryId is required")
            UUID categoryId,

            @NotNull(message = "amount is required")
            @DecimalMin(value = "0.01", message = "amount must be greater than 0")
            BigDecimal amount,

            @NotBlank(message = "currency is required")
            @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
            String currency,

            String description,

            @NotNull(message = "transactionDate is required")
            LocalDate transactionDate
    ) {}

    @Builder
    public record TransactionResponse(
            UUID id,
            UUID categoryId,
            String categoryName,
            TransactionType type,
            BigDecimal amount,
            String currency,
            String description,
            LocalDate transactionDate,
            LocalDateTime createdAt
    ) {}

    // ── Summary ───────────────────────────────────────────────────────────────

    @Builder
    public record PeriodSummary(
            String period,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal balance,
            java.util.List<CategorySummary> byCategory
    ) {}

    @Builder
    public record CategorySummary(
            UUID categoryId,
            String categoryName,
            TransactionType type,
            BigDecimal total,
            long transactionCount
    ) {}
}
