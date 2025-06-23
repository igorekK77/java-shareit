package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND (b.status = ?2 OR b.start < ?3) ORDER BY b.start DESC")
    List<Booking> findFilterBookerPast(Long userId, BookingStatus status, LocalDateTime beforeDate);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND (b.status = ?2 OR b.start > ?3) ORDER BY b.start DESC")
    List<Booking> findFilterBookerFuture(Long userId, BookingStatus status, LocalDateTime afterDate);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerId(Long userId);

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND (b.status = ?2 OR b.start < ?3) " +
            "ORDER BY b.start DESC")
    List<Booking> findFilterOwnerItemBookingPast(Long userId, BookingStatus status, LocalDateTime beforeDate);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND (b.status = ?2 OR b.start > ?3) " +
            "ORDER BY b.start DESC")
    List<Booking> findFilterOwnerItemBookingFuture(Long userId, BookingStatus status, LocalDateTime afterDate);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);
}
