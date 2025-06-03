package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status);
}
