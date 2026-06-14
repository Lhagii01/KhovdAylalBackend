package com.baruunaylal.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CampAnalyticsDTO {
    private Long totalRevenue;
    private Double averageCheck;
    private Integer activeBookings;
    private Integer totalGuests;
    private Integer mongolianGuests;
    private Integer foreignGuests;
    private Integer totalRooms;
    private Double occupancyRate;
    private List<Map<String, Object>> weeklyRevenue;
    private List<Map<String, Object>> monthlyRevenue;
    private Map<String, Integer> demographics;
    private Map<String, Integer> travelComposition;
    private List<Map<String, Object>> recentBookings;
}

