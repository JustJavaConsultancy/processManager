package tech.justjava.process_manager.invoice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tech.justjava.process_manager.invoice.domain.Invoice;
import tech.justjava.process_manager.invoice.domain.InvoiceStatus;
import tech.justjava.process_manager.invoice.repository.InvoiceRepository;
import tech.justjava.process_manager.util.ReferencedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class InvoiceServiceImpl implements InvoiceService, CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.debug("Checking if sample invoices need to be created...");
        if (invoiceRepository.count() == 0) {
            log.debug("No invoices found, creating sample data...");
            createSampleInvoices();
            log.debug("Sample invoices created successfully");
        } else {
            log.debug("Sample invoices already exist, skipping creation");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void createSampleInvoices() {
        String[] nigerianNames = {
            "Oluwaseun Adebayo", "Chidinma Okonkwo", "Babajide Ogunleye", "Aisha Mohammed",
            "Chukwudi Eze", "Folake Adeleke", "Obinna Nnamdi", "Yetunde Okafor",
            "Ibrahim Musa", "Ngozi Onyeka"
        };

        String[] emails = {
            "oluwaseun.adebayo@email.com", "chidinma.okonkwo@email.com", "babajide.ogunleye@email.com",
            "aisha.mohammed@email.com", "chukwudi.eze@email.com", "folake.adeleke@email.com",
            "obinna.nnamdi@email.com", "yetunde.okafor@email.com", "ibrahim.musa@email.com",
            "ngozi.onyeka@email.com"
        };

        String[] descriptions = {
            "Website development and deployment", "Mobile app UI/UX design", "Digital marketing campaign",
            "Software maintenance and support", "Cloud infrastructure setup", "Security audit and implementation",
            "Data analytics dashboard", "E-commerce platform development", "API integration services",
            "Training and documentation"
        };

        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 10; i++) {
            try {
                Invoice invoice = new Invoice();
                invoice.setClientName(nigerianNames[i]);
                invoice.setClientEmail(emails[i]);
                invoice.setAmount(BigDecimal.valueOf(random.nextInt(900000) + 100000));
                invoice.setDescription(descriptions[i]);
                invoice.setDueDate(now.plusDays(random.nextInt(60) + 1));

                int statusRandom = random.nextInt(10);
                InvoiceStatus status = statusRandom < 5 ? InvoiceStatus.PENDING :
                                   statusRandom < 7 ? InvoiceStatus.PAID :
                                   statusRandom < 9 ? InvoiceStatus.OVERDUE :
                                   InvoiceStatus.CANCELLED;
                invoice.setStatus(status);

                Invoice saved = createInvoice(invoice);
                log.debug("Created sample invoice: {} for client: {}", saved.getInvoiceNumber(), saved.getClientName());
            } catch (Exception e) {
                log.error("Error creating sample invoice {}: {}", i, e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        if (invoice.getInvoiceNumber() == null) {
            invoice.setInvoiceNumber(generateInvoiceNumber());
        }
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public Invoice updateInvoice(Long id, Invoice invoice) {
        return invoiceRepository.findById(id)
            .map(existingInvoice -> {
                existingInvoice.setClientName(invoice.getClientName());
                existingInvoice.setAmount(invoice.getAmount());
                existingInvoice.setDescription(invoice.getDescription());
                existingInvoice.setDueDate(invoice.getDueDate());
                existingInvoice.setStatus(invoice.getStatus());
                return invoiceRepository.save(existingInvoice);
            })
            .orElseThrow(() -> new ReferencedException("Invoice not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }

    @Override
    public String generateInvoiceNumber() {
        String prefix = "INV";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String random = String.format("%03d", new Random().nextInt(1000));
        String invoiceNumber = prefix + timestamp + random;
        
        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            random = String.format("%03d", new Random().nextInt(1000));
            invoiceNumber = prefix + timestamp + random;
        }
        
        return invoiceNumber;
    }
} 