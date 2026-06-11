package com.carlosdaza.financetracker.service;

import com.carlosdaza.financetracker.domain.dto.FinanceDto;
import com.carlosdaza.financetracker.domain.entity.Category;
import com.carlosdaza.financetracker.domain.entity.Transaction;
import com.carlosdaza.financetracker.domain.enums.TransactionType;
import com.carlosdaza.financetracker.exception.ResourceNotFoundException;
import com.carlosdaza.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional
    public FinanceDto.TransactionResponse create(FinanceDto.TransactionRequest request) {
        Category category = categoryService.findOrThrow(request.categoryId());
        Transaction tx = Transaction.builder()
                .category(category)
                .type(category.getType())   // type is derived from the category
                .amount(request.amount())
                .currency(request.currency())
                .description(request.description())
                .transactionDate(request.transactionDate())
                .build();
        return toResponse(transactionRepository.save(tx));
    }

    @Transactional(readOnly = true)
    public FinanceDto.TransactionResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public FinanceDto.TransactionResponse update(UUID id, FinanceDto.TransactionRequest request) {
        Transaction tx = findOrThrow(id);
        Category category = categoryService.findOrThrow(request.categoryId());
        tx.setCategory(category);
        tx.setType(category.getType());
        tx.setAmount(request.amount());
        tx.setCurrency(request.currency());
        tx.setDescription(request.description());
        tx.setTransactionDate(request.transactionDate());
        return toResponse(transactionRepository.save(tx));
    }

    @Transactional
    public void delete(UUID id) {
        findOrThrow(id);
        transactionRepository.deleteById(id);
    }

    // ── Period queries ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public FinanceDto.PeriodSummary getByDay(LocalDate date) {
        List<Transaction> transactions = transactionRepository
                .findByTransactionDateOrderByCreatedAtDesc(date);
        return buildSummary(date.toString(), date, date, transactions);
    }

    @Transactional(readOnly = true)
    public FinanceDto.PeriodSummary getByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();
        List<Transaction> transactions = transactionRepository.findByYearAndMonth(year, month);
        return buildSummary(year + "-" + String.format("%02d", month), from, to, transactions);
    }

    @Transactional(readOnly = true)
    public FinanceDto.PeriodSummary getByYear(int year) {
        LocalDate from = Year.of(year).atDay(1);
        LocalDate to = LocalDate.of(year, 12, 31);
        List<Transaction> transactions = transactionRepository.findByYear(year);
        return buildSummary(String.valueOf(year), from, to, transactions);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private FinanceDto.PeriodSummary buildSummary(
            String label, LocalDate from, LocalDate to, List<Transaction> transactions) {

        BigDecimal totalIncome = transactionRepository
                .sumByTypeAndDateBetween(TransactionType.INCOME, from, to);
        BigDecimal totalExpense = transactionRepository
                .sumByTypeAndDateBetween(TransactionType.EXPENSE, from, to);

        List<Object[]> rawSummary = transactionRepository.summarizeByCategory(from, to);
        List<FinanceDto.CategorySummary> byCat = rawSummary.stream().map(row ->
                FinanceDto.CategorySummary.builder()
                        .categoryId((UUID) row[0])
                        .categoryName((String) row[1])
                        .type((TransactionType) row[2])
                        .total((BigDecimal) row[3])
                        .transactionCount((Long) row[4])
                        .build()
        ).toList();

        return FinanceDto.PeriodSummary.builder()
                .period(label)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .byCategory(byCat)
                .build();
    }

    private Transaction findOrThrow(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id.toString()));
    }

    private FinanceDto.TransactionResponse toResponse(Transaction t) {
        return FinanceDto.TransactionResponse.builder()
                .id(t.getId())
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .type(t.getType())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .description(t.getDescription())
                .transactionDate(t.getTransactionDate())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
