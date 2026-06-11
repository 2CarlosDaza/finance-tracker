package com.carlosdaza.financetracker.controller;

import com.carlosdaza.financetracker.domain.dto.FinanceDto;
import com.carlosdaza.financetracker.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Record and query income and expense transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Register a new transaction")
    public ResponseEntity<FinanceDto.TransactionResponse> create(
            @Valid @RequestBody FinanceDto.TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<FinanceDto.TransactionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a transaction")
    public ResponseEntity<FinanceDto.TransactionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody FinanceDto.TransactionRequest request) {
        return ResponseEntity.ok(transactionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Period summary endpoints ───────────────────────────────────────────────

    @GetMapping("/summary/day")
    @Operation(summary = "Get transactions and totals for a specific day")
    public ResponseEntity<FinanceDto.PeriodSummary> getByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(transactionService.getByDay(date));
    }

    @GetMapping("/summary/month")
    @Operation(summary = "Get transactions and totals for a specific month")
    public ResponseEntity<FinanceDto.PeriodSummary> getByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(transactionService.getByMonth(year, month));
    }

    @GetMapping("/summary/year")
    @Operation(summary = "Get transactions and totals for a full year")
    public ResponseEntity<FinanceDto.PeriodSummary> getByYear(@RequestParam int year) {
        return ResponseEntity.ok(transactionService.getByYear(year));
    }
}
