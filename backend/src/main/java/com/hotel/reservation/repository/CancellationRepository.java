package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Cancellation;
import com.hotel.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CancellationRepository extends JpaRepository<Cancellation, Long> {
    Optional<Cancellation> findByReservation(Reservation reservation);
}
