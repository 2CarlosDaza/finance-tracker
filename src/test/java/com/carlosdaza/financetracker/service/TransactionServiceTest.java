package com.carlosdaza.financetracker.service;

import com.carlosdaza.financetracker.domain.dto.FinanceDto;
import com.carlosdaza.financetracker.domain.entity.Category;
import com.carlosdaza.financetracker.domain.entity.Transaction;
import com.carlosdaza.financetracker.domain.enums.TransactionType;
import com.carlosdaza.financetracker.exception.ResourceNotFoundException;
import com.carlosdaza.financetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CategoryService categoryService;
    @InjectMocks private TransactionService transactionService;

    private Category expenseCategory;
    private Category incomeCategory;
    private UUID txId;

    @BeforeEach
    void setUp() {
        expenseCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Food")
                .type(TransactionType.EXPENSE)
                .createdAt(LocalDateTime.now())
                .build();

        incomeCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Salary")
                .type(TransactionType.INCOME)
                .createdAt(LocalDateTime.now())
                .build();

        txId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create expense transaction with type derived from category")
    void create_shouldDeriveTypeFromCategory() {
        FinanceDto.TransactionRequest request = new FinanceDto.TransactionRequest(
                expenseCategory.getId(), new BigDecimal("50.00"), "USD",
                "Lunch", LocalDate.now()
        );

        Transaction saved = Transaction.builder()
                .id(txId).category(expenseCategory).type(TransactionType.EXPENSE)
                .amount(new BigDecimal("50.00")).currency("USD")
                .description("Lunch").transactionDate(LocalDate.now())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(categoryService.findOrThrow(expenseCategory.getId())).thenReturn(expenseCategory);
        when(transactionRepository.save(any())).thenReturn(saved);

        FinanceDto.TransactionResponse response = transactionService.create(request);

        assertThat(response.type()).isEqualTo(TransactionType.EXPENSE);
        assertThat(response.categoryName()).isEqualTo("Food");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when transaction does not exist")
    void getById_shouldThrowWhenNotFound() {
        UUID nonExistent = UUID.randomUUID();
        when(transactionRepository.findById(nonExistent)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getById(nonExistent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistent.toString());
    }

    @Test
    @DisplayName("Should build day summary with totals from repository")
    void getByDay_shouldReturnSummaryWithTotals() {
        LocalDate today = LocalDate.now();

        when(transactionRepository.findByTransactionDateOrderByCreatedAtDesc(today))
                .thenReturn(List.of());
        when(transactionRepository.sumByTypeAndDateBetween(TransactionType.INCOME, today, today))
                .thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.sumByTypeAndDateBetween(TransactionType.EXPENSE, today, today))
                .thenReturn(new BigDecimal("350.00"));
        when(transactionRepository.summarizeByCategory(today, today))
                .thenReturn(List.of());

        FinanceDto.PeriodSummary summary = transactionService.getByDay(today);

        assertThat(summary.totalIncome()).isEqualByComparingTo("1000.00");
        assertThat(summary.totalExpense()).isEqualByComparingTo("350.00");
        assertThat(summary.balance()).isEqualByComparingTo("650.00");
        assertThat(summary.period()).isEqualTo(today.toString());
    }

    @Test
    @DisplayName("Should delete transaction when it exists")
    void delete_shouldRemoveTransaction() {
        Transaction tx = Transaction.builder()
                .id(txId).category(expenseCategory).type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN).currency("USD")
                .transactionDate(LocalDate.now())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findById(txId)).thenReturn(Optional.of(tx));

        transactionService.delete(txId);

        verify(transactionRepository).deleteById(txId);
    }
}
