package tech.justjava.process_manager.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tech.justjava.process_manager.invoice.domain.Invoice;
import tech.justjava.process_manager.invoice.service.InvoiceService;

import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {
    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public String listInvoices(Model model) {
        log.debug("Showing invoice list page");
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            log.debug("Found {} invoices", invoices.size());
            model.addAttribute("invoices", invoices);
            return "invoice/index";
        } catch (Exception e) {
            log.error("Error showing invoice list page", e);
            model.addAttribute("error", "Error showing invoice list: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/create")
    public String showCreateForm() {
        log.debug("Showing create invoice form");
        return "invoice/create";
    }

    @PostMapping("/create")
    public String createInvoice(@ModelAttribute Invoice invoice) {
        log.debug("Creating new invoice for client: {}", invoice.getClientName());
        try {
            invoiceService.createInvoice(invoice);
            return "redirect:/invoices?success=Invoice created successfully";
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            return "redirect:/invoices/create?error=" + e.getMessage();
        }
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        log.debug("Viewing invoice with id: {}", id);
        try {
            return invoiceService.getInvoiceById(id)
                .map(invoice -> {
                    model.addAttribute("invoice", invoice);
                    return "invoice/view";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invoice not found with id: " + id);
                    return "error";
                });
        } catch (Exception e) {
            log.error("Error viewing invoice", e);
            model.addAttribute("error", "Error viewing invoice: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String editInvoiceForm(@PathVariable Long id, Model model) {
        log.debug("Showing edit form for invoice with id: {}", id);
        try {
            return invoiceService.getInvoiceById(id)
                .map(invoice -> {
                    model.addAttribute("invoice", invoice);
                    return "invoice/edit";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invoice not found with id: " + id);
                    return "error";
                });
        } catch (Exception e) {
            log.error("Error showing edit form", e);
            model.addAttribute("error", "Error showing edit form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateInvoice(@PathVariable Long id, @ModelAttribute Invoice invoice) {
        log.debug("Updating invoice with id: {}", id);
        try {
            invoiceService.updateInvoice(id, invoice);
            return "redirect:/invoices?success=Invoice updated successfully";
        } catch (Exception e) {
            log.error("Error updating invoice", e);
            return "redirect:/invoices?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable Long id) {
        log.debug("Deleting invoice with id: {}", id);
        try {
            invoiceService.deleteInvoice(id);
            return "redirect:/invoices?success=Invoice deleted successfully";
        } catch (Exception e) {
            log.error("Error deleting invoice", e);
            return "redirect:/invoices?error=" + e.getMessage();
        }
    }
} 