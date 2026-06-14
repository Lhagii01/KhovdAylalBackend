package com.baruunaylal.backend.impl;

import com.baruunaylal.backend.dto.CampAnalyticsDTO;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.RoomRepository;
import com.baruunaylal.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// ЭНЭ ХЭСГИЙГ ЗААВАЛ ШАЛГААРАЙ: DayOfWeek-ийг танихгүй байгаа тул import нэмлээ.
import java.time.DayOfWeek;
import static java.time.DayOfWeek.*;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public CampAnalyticsDTO getCampDashboardStats(Long campId) {
        Double totalRev = bookingRepository.sumAllTotalPriceByCampId(campId);
        long revenue = totalRev != null ? totalRev.longValue() : 0L;

        LocalDate today = LocalDate.now();
        List<Map<String, Object>> weeklyRevenueData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Double dayRev = bookingRepository.sumRevenueByDate(campId, date);

            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("name", getDayName(date));
            dayMap.put("date", date.toString());
            dayMap.put("revenue", dayRev != null ? dayRev : 0.0);
            weeklyRevenueData.add(dayMap);
        }

        List<Map<String, Object>> monthlyRevenueData = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime start = month.atDay(1).atStartOfDay();
            LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();
            Double monthRev = bookingRepository.sumRevenueBetween(campId, start, end);

            Map<String, Object> monthMap = new HashMap<>();
            monthMap.put("monthKey", month.toString());
            monthMap.put("name", month.getMonthValue() + "-р сар");
            monthMap.put("revenue", monthRev != null ? monthRev : 0.0);
            monthlyRevenueData.add(monthMap);
        }

        long totalBookings = bookingRepository.countByRoom_Camp_Id(campId);
        double avgCheck = totalBookings > 0 ? (double) revenue / totalBookings : 0.0;
        long activeNow = bookingRepository.countActiveBookingsByDate(campId, today);

        Integer adults = bookingRepository.sumAdultsByCampId(campId);
        Integer children = bookingRepository.sumChildrenByCampId(campId);

        int totalAdult = adults != null ? adults : 0;
        int totalChild = children != null ? children : 0;
        int totalGuests = totalAdult + totalChild;

        Map<String, Integer> demographics = new HashMap<>();
        demographics.put("adults", totalAdult);
        demographics.put("children", totalChild);

        int totalRooms = roomRepository.findByCamp_Id(campId).size();
        double occupancy = totalRooms > 0 ? (activeNow * 100.0) / totalRooms : 0.0;

        List<Booking> allCampBookings = bookingRepository.findByRoom_Camp_IdOrderByBookingDateDesc(campId);

        int mongolianGuests = 0;
        int foreignGuests = 0;
        int familyTrips = 0;
        int friendTrips = 0;
        int teamTrips = 0;
        int soloTrips = 0;
        int otherTrips = 0;

        for (Booking booking : allCampBookings) {
            int bookingAdults = booking.getAdultCount() != null ? booking.getAdultCount() : 0;
            int bookingChildren = booking.getChildCount() != null ? booking.getChildCount() : 0;
            int bookingGuests = Math.max(1, bookingAdults + bookingChildren);

            String nationality = booking.getNationality() != null ? booking.getNationality().trim().toLowerCase(Locale.ROOT) : "";
            if (nationality.contains("mongol") || nationality.contains("монгол")) {
                mongolianGuests += bookingGuests;
            } else {
                foreignGuests += bookingGuests;
            }

            String comment = booking.getComment() != null ? booking.getComment().toLowerCase(Locale.ROOT) : "";
            String travelType = extractTravelType(comment);
            int roomCount = extractIntTag(comment, "[roomcount:");
            boolean mentionsTeam = comment.contains("хамт олон") || comment.contains("team") || comment.contains("company") || comment.contains("group");
            boolean mentionsFamily = comment.contains("гэр бүл") || comment.contains("family") || bookingChildren > 0;
            boolean mentionsFriend = comment.contains("найз") || comment.contains("friend");

            if ("team".equals(travelType) || mentionsTeam || bookingGuests >= 6 || roomCount >= 2) {
                teamTrips++;
            } else if ("family".equals(travelType) || mentionsFamily || bookingGuests >= 3) {
                familyTrips++;
            } else if ("friends".equals(travelType) || mentionsFriend || bookingGuests == 2) {
                friendTrips++;
            } else if ("solo".equals(travelType) || bookingGuests == 1) {
                soloTrips++;
            } else {
                otherTrips++;
            }
        }

        Map<String, Integer> travelComposition = new HashMap<>();
        travelComposition.put("family", familyTrips);
        travelComposition.put("friends", friendTrips);
        travelComposition.put("pet", teamTrips);
        travelComposition.put("solo", soloTrips);
        travelComposition.put("other", otherTrips);

        List<Booking> recent = bookingRepository.findTop10ByRoom_Camp_IdOrderByBookingDateDesc(campId);
        List<Map<String, Object>> recentBookings = recent.stream().map(item -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", item.getId());
            row.put("customerName", item.getCustomerName());
            row.put("phoneNumber", item.getPhoneNumber());
            row.put("status", item.getStatus());
            row.put("totalPrice", item.getTotalPrice());
            row.put("bookingDate", item.getBookingDate());
            row.put("checkInDate", item.getCheckInDate());
            row.put("checkOutDate", item.getCheckOutDate());
            row.put("adultCount", item.getAdultCount());
            row.put("childCount", item.getChildCount());
            row.put("nationality", item.getNationality());
            row.put("comment", item.getComment());
            row.put("roomNumber", item.getRoom() != null ? item.getRoom().getRoomNumber() : null);
            return row;
        }).toList();

        return CampAnalyticsDTO.builder()
                .totalRevenue(revenue)
                .averageCheck(avgCheck)
                .activeBookings((int) activeNow)
                .totalGuests(totalGuests)
                .mongolianGuests(mongolianGuests)
                .foreignGuests(foreignGuests)
                .totalRooms(totalRooms)
                .occupancyRate(occupancy > 100 ? 100.0 : occupancy)
                .demographics(demographics)
                .travelComposition(travelComposition)
                .weeklyRevenue(weeklyRevenueData)
                .monthlyRevenue(monthlyRevenueData)
                .recentBookings(recentBookings)
                .build();
    }

    private String getDayName(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "Да";
            case TUESDAY -> "Мя";
            case WEDNESDAY -> "Лха";
            case THURSDAY -> "Пү";
            case FRIDAY -> "Ба";
            case SATURDAY -> "Бя";
            case SUNDAY -> "Ня";
        };
    }

    private String extractTravelType(String comment) {
        if (comment == null) {
            return "";
        }
        String marker = "[traveltype:";
        int start = comment.indexOf(marker);
        if (start < 0) {
            return "";
        }
        int valueStart = start + marker.length();
        int valueEnd = comment.indexOf("]", valueStart);
        if (valueEnd < 0) {
            return "";
        }
        return comment.substring(valueStart, valueEnd).trim();
    }

    private int extractIntTag(String comment, String marker) {
        if (comment == null) {
            return 0;
        }
        int start = comment.indexOf(marker);
        if (start < 0) {
            return 0;
        }
        int valueStart = start + marker.length();
        int valueEnd = comment.indexOf("]", valueStart);
        if (valueEnd < 0) {
            return 0;
        }
        try {
            return Integer.parseInt(comment.substring(valueStart, valueEnd).trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
