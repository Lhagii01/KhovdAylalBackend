package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 1. ХЭРЭГЛЭГЧИЙН ХЭСЭГ: Өөрийнх нь захиалгууд
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.bookingDate DESC")
    List<Booking> findByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM bookings b WHERE b.user_id = :userId ORDER BY b.booking_date DESC", nativeQuery = true)
    List<Booking> findByUserIdNative(@Param("userId") Long userId);
    
    @Query(value = """
            SELECT * FROM bookings b
            WHERE b.user_id = :userId
               OR (
                    COALESCE(:phone, '') <> ''
                    AND REPLACE(REPLACE(REPLACE(COALESCE(b.phone_number, ''), ' ', ''), '-', ''), '+', '')
                        = REPLACE(REPLACE(REPLACE(:phone, ' ', ''), '-', ''), '+', '')
               )
            ORDER BY b.booking_date DESC
            """, nativeQuery = true)
    List<Booking> findByUserIdOrPhoneFlexible(@Param("userId") Long userId, @Param("phone") String phone);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE bookings b
            SET b.user_id = :userId
            WHERE b.user_id IS NULL
              AND COALESCE(:phone, '') <> ''
              AND REPLACE(REPLACE(REPLACE(COALESCE(b.phone_number, ''), ' ', ''), '-', ''), '+', '')
                    = REPLACE(REPLACE(REPLACE(:phone, ' ', ''), '-', ''), '+', '')
            """, nativeQuery = true)
    int bindUnassignedBookingsToUserByPhone(@Param("userId") Long userId, @Param("phone") String phone);

    // 2. МЕНЕЖЕРИЙН ХЭСЭГ: Баазын бүх захиалга
    // Таны Entity дээр campId эсвэл room.camp.id-аар хайх шаардлагатай
    long countByRoom_Camp_Id(Long campId);
    List<Booking> findByRoom_Camp_IdOrderByBookingDateDesc(Long campId);
    List<Booking> findTop5ByRoom_Camp_IdOrderByBookingDateDesc(Long campId);
    List<Booking> findTop10ByRoom_Camp_IdOrderByBookingDateDesc(Long campId);

    // Аль нэг нь байхад хангалттай (Flexible хувилбар)
    @Query("""
            SELECT b FROM Booking b
            WHERE (b.room IS NOT NULL AND b.room.camp.id = :campId)
               OR (b.room IS NULL AND b.camp.id = :campId)
            ORDER BY b.bookingDate DESC
            """)
    List<Booking> findAllByCampIdFlexible(@Param("campId") Long campId);

    // 3. СТАТИСТИК ХЭСЭГ
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.room.camp.id = :campId")
    Double sumAllTotalPriceByCampId(@Param("campId") Long campId);
    
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE (b.room.camp.id = :campId OR b.camp.id = :campId) AND UPPER(COALESCE(b.status, '')) = UPPER(:status)")
    Double sumTotalPriceByCampIdAndStatus(@Param("campId") Long campId, @Param("status") String status);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE (b.room.camp.id = :campId OR b.camp.id = :campId) AND function('date', b.bookingDate) = :date")
    Double sumRevenueByDate(@Param("campId") Long campId, @Param("date") LocalDate date);
    
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE (b.room.camp.id = :campId OR b.camp.id = :campId) AND b.bookingDate >= :startDate AND b.bookingDate < :endDate")
    Double sumRevenueBetween(@Param("campId") Long campId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT SUM(b.adultCount) FROM Booking b WHERE b.room.camp.id = :campId")
    Integer sumAdultsByCampId(@Param("campId") Long campId);

    @Query("SELECT SUM(b.childCount) FROM Booking b WHERE b.room.camp.id = :campId")
    Integer sumChildrenByCampId(@Param("campId") Long campId);

    long countByRoom_Camp_IdAndStatus(Long campId, String status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.camp.id = :campId AND b.checkInDate <= :today AND b.checkOutDate >= :today")
    long countActiveBookingsByDate(@Param("campId") Long campId, @Param("today") LocalDate today);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.room.id = :roomId
              AND UPPER(COALESCE(b.status, '')) <> 'CANCELLED'
              AND b.checkInDate < :checkOutDate
              AND b.checkOutDate > :checkInDate
            """)
    List<Booking> findOverlappingBookingsForRoom(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    // 4. СЭТГЭГДЭЛТЭЙ ЗАХИАЛГУУД
    @Query("SELECT b FROM Booking b WHERE (b.room.camp.id = :campId OR b.camp.id = :campId) " +
            "AND b.comment IS NOT NULL AND TRIM(b.comment) <> '' ORDER BY b.bookingDate DESC")
    List<Booking> findBookingsWithCommentByCampId(@Param("campId") Long campId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE (b.room.camp.id = :campId OR b.camp.id = :campId) " +
            "AND b.comment IS NOT NULL AND TRIM(b.comment) <> ''")
    long countBookingsWithCommentByCampId(@Param("campId") Long campId);
}
