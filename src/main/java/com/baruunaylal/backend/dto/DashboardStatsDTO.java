package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.dto.TravelerCommentDTO;
import lombok.Builder;
import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Double occupancyRate;
    private Integer adultCount;
    private Integer childCount;
    private Long totalComments;
    private Map<String, Long> roomStatus;
    private List<TravelerCommentDTO> recentComments;
}