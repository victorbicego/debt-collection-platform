package dev.debtcollection.invoices;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "invoice")
public class Invoice {

    public enum Status { OPEN, PAID, CANCELLED }

    @Id
    private UUID id;

    @Column(nullable = false)
    private String debtor;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Invoice() {
        // required by JPA
    }

    public Invoice(String debtor, BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "amount");
        if (debtor == null || debtor.isBlank()) {
            throw new IllegalArgumentException("debtor must not be blank");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        this.id = UUID.randomUUID();
        this.debtor = debtor;
        this.amount = amount;
        this.currency = currency;
        this.status = Status.OPEN;
        this.createdAt = Instant.now();
    }

    public void markPaid() {
        if (status != Status.OPEN) {
            throw new IllegalStateException("only an open invoice can be paid, current status: " + status);
        }
        status = Status.PAID;
    }

    public void cancel() {
        if (status == Status.PAID) {
            throw new IllegalStateException("a paid invoice cannot be cancelled");
        }
        status = Status.CANCELLED;
    }

    public UUID getId() { return id; }
    public String getDebtor() { return debtor; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
