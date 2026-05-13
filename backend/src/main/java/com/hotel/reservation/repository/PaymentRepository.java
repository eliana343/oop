package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Invoice;
import com.hotel.reservation.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice(Invoice invoice);
}
