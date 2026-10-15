package dev.debtcollection.invoices;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Domain rules only: no Spring, no database. */
class InvoiceTest {

    private Invoice openInvoice() {
        return new Invoice("Jane Example", new BigDecimal("150.00"), "EUR");
    }

    @Test
    void startsOpen() {
        assertThat(openInvoice().getStatus()).isEqualTo(Invoice.Status.OPEN);
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> new Invoice("Jane Example", BigDecimal.ZERO, "EUR"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBlankDebtor() {
        assertThatThrownBy(() -> new Invoice(" ", BigDecimal.TEN, "EUR"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void openInvoiceCanBePaid() {
        var invoice = openInvoice();
        invoice.markPaid();
        assertThat(invoice.getStatus()).isEqualTo(Invoice.Status.PAID);
    }

    @Test
    void paidInvoiceCannotBeCancelled() {
        var invoice = openInvoice();
        invoice.markPaid();
        assertThatThrownBy(invoice::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelledInvoiceCannotBePaid() {
        var invoice = openInvoice();
        invoice.cancel();
        assertThatThrownBy(invoice::markPaid).isInstanceOf(IllegalStateException.class);
    }
}
