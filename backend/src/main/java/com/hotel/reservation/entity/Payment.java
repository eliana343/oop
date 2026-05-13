package com.hotel.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(nullable = false, length = 64)
    private String paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(nullable = false, length = 32)
    private String paymentStatus = "PENDING";

    public Payment() {
    }

    public Payment(Invoice invoice, String paymentMethod, BigDecimal amountPaid, String paymentStatus) {
        this.invoice = invoice;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus;
    }

    public String processPayment() {
        return "Payment " + paymentStatus + " via " + paymentMethod + " amount $" + amountPaid.toPlainString();
    }

    public String displayReceipt() {
        String change = "";
        if ("CASH".equalsIgnoreCase(paymentMethod) && invoice != null) {
            BigDecimal due = invoice.getTotalAmount();
            BigDecimal diff = amountPaid.subtract(due);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                change = " | Change: $" + diff.toPlainString();
            }
        }
        return processPayment() + change;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
