package dev.debtcollection.invoices;

import java.util.UUID;

class InvoiceNotFoundException extends RuntimeException {

    InvoiceNotFoundException(UUID id) {
        super("Invoice " + id + " not found");
    }
}
