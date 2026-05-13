package com.hotel.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    private Reservation reservation;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRoomCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalServiceCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public Invoice() {
    }

    public Invoice(Reservation reservation) {
        setReservation(reservation);
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            reservation.setInvoice(this);
        }
    }

    public BigDecimal calculateTotal() {
        this.totalAmount = totalRoomCost.add(totalServiceCost);
        return totalAmount;
    }

    public String generateInvoice() {
        calculateTotal();
        return displayInvoice();
    }

    public String displayInvoice() {
        return String.format(
                "Invoice for reservation %s | Rooms: $%s | Services: $%s | Total: $%s",
                reservation != null ? reservation.getReservationId() : "?",
                totalRoomCost.toPlainString(),
                totalServiceCost.toPlainString(),
                totalAmount.toPlainString()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public BigDecimal getTotalRoomCost() {
        return totalRoomCost;
    }

    public void setTotalRoomCost(BigDecimal totalRoomCost) {
        this.totalRoomCost = totalRoomCost;
    }

    public BigDecimal getTotalServiceCost() {
        return totalServiceCost;
    }

    public void setTotalServiceCost(BigDecimal totalServiceCost) {
        this.totalServiceCost = totalServiceCost;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
