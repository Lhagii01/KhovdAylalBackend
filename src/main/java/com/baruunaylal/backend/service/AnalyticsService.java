package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.CampAnalyticsDTO;

public interface AnalyticsService {
    CampAnalyticsDTO getCampDashboardStats(Long campId);
}