package com.hotel.reservation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cancellation")
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    private Reservation reservation;

    @Column(length = 2000)
    private String cancellationReason;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cancellationFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    public Cancellation() {
    }

    public Cancellation(Reservation reservation, String cancellationReason) {
        this.reservation = reservation;
        this.cancellationReason = cancellationReason;
    }

    public String cancelReservation() {
        return "Reservation " + (reservation != null ? reservation.getReservationId() : "?") + " cancelled.";
    }

    public BigDecimal calculateRefund(BigDecimal paidTotal) {
        if (paidTotal == null) {
            paidTotal = BigDecimal.ZERO;
        }
        BigDecimal net = paidTotal.subtract(cancellationFee);
        this.refundAmount = net.max(BigDecimal.ZERO);
        return refundAmount;
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

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public BigDecimal getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(BigDecimal cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
}
