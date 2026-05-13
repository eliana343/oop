package com.hotel.reservation.repository;

import com.hotel.reservation.entity.OfferedService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferedServiceRepository extends JpaRepository<OfferedService, Long> {
    List<OfferedService> findByIdIn(List<Long> ids);
}
