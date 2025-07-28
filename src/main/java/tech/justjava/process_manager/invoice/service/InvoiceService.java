package tech.justjava.process_manager.invoice.service;

import tech.justjava.process_manager.invoice.domain.Invoice;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<Invoice> getAllInvoices();
    Optional<Invoice> getInvoiceById(Long id);
    Invoice createInvoice(Invoice invoice);
    Invoice updateInvoice(Long id, Invoice invoice);
    void deleteInvoice(Long id);
    String generateInvoiceNumber();
} 