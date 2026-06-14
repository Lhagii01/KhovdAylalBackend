package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.DashboardStatsDTO;
import com.baruunaylal.backend.dto.TravelerCommentDTO;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.entity.TouristCamp;
import com.baruunaylal.backend.entity.Comment;
import com.baruunaylal.backend.entity.Room;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import com.baruunaylal.backend.repository.CommentRepository;
import com.baruunaylal.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/camp-admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CampAdminController {

    private final BookingRepository bookingRepository;
    private final TouristCampRepository campRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    // 1. Үндсэн статистик (Dashboard)
    @GetMapping("/stats/{adminId}")
    public ResponseEntity<Map<String, Object>> getCampAdminStats(@PathVariable Long adminId) {
        log.info("Fetching basic stats for adminId: {}", adminId);

        TouristCamp camp = findCampByAdminOrOwner(adminId);
        if (camp == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Танд бүртгэлтэй бааз олдсонгүй!"));
        }

        long totalBookings = bookingRepository.countByRoom_Camp_Id(camp.getId());
        long pendingBookings = bookingRepository.countByRoom_Camp_IdAndStatus(camp.getId(), "PENDING");

        List<Booking> recentOrders = bookingRepository.findTop5ByRoom_Camp_IdOrderByBookingDateDesc(camp.getId());
        List<Map<String, Object>> recentBookingsDTO = recentOrders.stream().map(this::mapToBookingMap).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("id", camp.getId());
        response.put("campName", camp.getName());
        response.put("totalBookings", totalBookings);
        response.put("pendingBookings", pendingBookings);
        response.put("recentBookings", recentBookingsDTO);
        response.put("totalRooms", (camp.getRooms() != null) ? camp.getRooms().size() : 0);
        response.put("soumName", (camp.getSoum() != null) ? camp.getSoum().getName() : "Тодорхойгүй");

        return ResponseEntity.ok(response);
    }

    // 2. БҮХ Сэтгэгдлүүдийг DTO хэлбэрээр татах (Frontend-д зориулсан)
    @GetMapping("/{adminId}/all-comments")
    public ResponseEntity<List<TravelerCommentDTO>> getAllCampComments(@PathVariable Long adminId) {
        log.info("Fetching all traveler comments for adminId: {}", adminId);
        TouristCamp camp = findCampByAdminOrOwner(adminId);
        if (camp == null) return ResponseEntity.notFound().build();

        // Сэтгэгдлийн үйлчилгээнээс хязгаарлалтгүй бүх сэтгэгдлийг татах
        List<TravelerCommentDTO> allComments = commentService.getTravelerCommentsByCampId(camp.getId());
        return ResponseEntity.ok(allComments);
    }

    // 3. Өрөөнүүдийн жагсаалтыг татах
    @GetMapping("/{adminId}/rooms")
    public ResponseEntity<List<Room>> getCampRooms(@PathVariable Long adminId) {
        log.info("Fetching rooms for adminId: {}", adminId);
        TouristCamp camp = findCampByAdminOrOwner(adminId);
        if (camp == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(camp.getRooms());
    }

    // 4. Өргөтгөсөн статистик
    @GetMapping("/stats/{adminId}/extended")
    public ResponseEntity<DashboardStatsDTO> getExtendedStats(@PathVariable Long adminId) {
        log.info("Fetching extended stats with comments for adminId: {}", adminId);

        TouristCamp camp = findCampByAdminOrOwner(adminId);
        if (camp == null) return ResponseEntity.status(404).build();

        Long campId = camp.getId();

        Double revenue = bookingRepository.sumTotalPriceByCampIdAndStatus(campId, "CONFIRMED");
        Integer adults = bookingRepository.sumAdultsByCampId(campId);
        Integer children = bookingRepository.sumChildrenByCampId(campId);

        int totalRooms = (camp.getRooms() != null) ? camp.getRooms().size() : 0;
        long occupiedToday = bookingRepository.countActiveBookingsByDate(campId, LocalDate.now());
        double occupancy = (totalRooms > 0) ? ((double) occupiedToday / totalRooms) * 100 : 0;

        long commentCount = commentRepository.countByCampId(campId);
        long bookingCommentCount = bookingRepository.countBookingsWithCommentByCampId(campId);
        long totalCommentCount = commentCount + bookingCommentCount;

        // Dashboard дээр зөвхөн сүүлийн 5-ыг харуулна
        List<TravelerCommentDTO> recentComments = commentService.getTravelerCommentsByCampId(campId)
                .stream()
                .limit(5)
                .toList();

        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalRevenue(revenue != null ? revenue : 0.0)
                .occupancyRate(Math.round(occupancy * 10.0) / 10.0)
                .adultCount(adults != null ? adults : 0)
                .childCount(children != null ? children : 0)
                .totalComments(totalCommentCount)
                .recentComments(recentComments)
                .build();

        return ResponseEntity.ok(stats);
    }

    // 5. Устгах функцүүд
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        log.info("Deleting commentId: {}", commentId);
        return commentRepository.findById(commentId)
                .map(comment -> {
                    commentRepository.delete(comment);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bookings/{bookingId}/comment")
    public ResponseEntity<?> deleteBookingComment(@PathVariable Long bookingId) {
        log.info("Deleting booking comment for bookingId: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    booking.setComment(null);
                    bookingRepository.save(booking);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private TouristCamp findCampByAdminOrOwner(Long id) {
        List<TouristCamp> camps = campRepository.findByAdminId(id);
        if (!camps.isEmpty()) return camps.get(0);
        return campRepository.findByOwnerId(id).orElse(null);
    }

    private Map<String, Object> mapToBookingMap(Booking order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("customerName", order.getCustomerName() != null ? order.getCustomerName() : "Нэргүй");
        map.put("bookingDate", order.getBookingDate());
        map.put("status", order.getStatus());
        map.put("totalPrice", order.getTotalPrice());
        map.put("phoneNumber", order.getPhoneNumber());
        map.put("adultCount", order.getAdultCount());
        map.put("childCount", order.getChildCount());
        return map;
    }
}