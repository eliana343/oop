package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Invoice;
import com.hotel.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByReservation(Reservation reservation);
}
