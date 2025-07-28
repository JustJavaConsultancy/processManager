package tech.justjava.process_manager.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.process_manager.invoice.domain.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByInvoiceNumber(String invoiceNumber);
} 