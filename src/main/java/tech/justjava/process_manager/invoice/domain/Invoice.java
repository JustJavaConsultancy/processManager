package tech.justjava.process_manager.invoice.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 1000)
    private String description;

    @Column
    private LocalDateTime dueDate;

    @Column
    private LocalDateTime issuedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (issuedDate == null) {
            issuedDate = LocalDateTime.now();
        }
    }
} 