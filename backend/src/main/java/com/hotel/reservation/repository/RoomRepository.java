package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailabilityStatusIgnoreCase(String status);

    List<Room> findByIdIn(List<Long> ids);
}
