package dev.debtcollection.invoices;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/invoices")
class InvoiceController {

    record NewInvoice(
            @NotBlank String debtor,
            @NotNull @Positive BigDecimal amount,
            @NotNull @Pattern(regexp = "[A-Z]{3}") String currency) {}

    record InvoiceResponse(UUID id, String debtor, BigDecimal amount, String currency,
                           Invoice.Status status, Instant createdAt) {
        static InvoiceResponse from(Invoice invoice) {
            return new InvoiceResponse(invoice.getId(), invoice.getDebtor(), invoice.getAmount(),
                    invoice.getCurrency(), invoice.getStatus(), invoice.getCreatedAt());
        }
    }

    private final InvoiceRepository repository;

    InvoiceController(InvoiceRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    ResponseEntity<InvoiceResponse> register(@Valid @RequestBody NewInvoice request) {
        var invoice = repository.save(new Invoice(request.debtor(), request.amount(), request.currency()));
        return ResponseEntity.created(URI.create("/invoices/" + invoice.getId()))
                .body(InvoiceResponse.from(invoice));
    }

    @GetMapping("/{id}")
    InvoiceResponse get(@PathVariable UUID id) {
        return repository.findById(id)
                .map(InvoiceResponse::from)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
    }
}
