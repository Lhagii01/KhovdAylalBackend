package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import com.baruunaylal.backend.dto.RecentBookingDTO;

@Data
@AllArgsConstructor
public class DashboardDataDTO {
    private long totalCamps;
    private long totalBookings;
    private long totalUsers;
    private double totalRevenue;
    private long totalMessages;
    private List<RecentBookingDTO> recentBookings; // Сүүлийн 5-10 захиалга
}