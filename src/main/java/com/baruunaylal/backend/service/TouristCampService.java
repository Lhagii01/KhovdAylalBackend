package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.TouristCampDTO;
import com.baruunaylal.backend.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface TouristCampService {
    List<TouristCampDTO> getAllCamps();
    List<TouristCampDTO> getCampsByUser(User user);
    List<TouristCampDTO> getCampsByOwnerId(Long ownerId);
    TouristCampDTO getCampById(Long id);

    // Үүсгэх
    TouristCampDTO createCamp(TouristCampDTO dto, MultipartFile image, User user);

    // 🔥 ЗАССАН: Зураг болон Хэрэглэгчийг давхар хүлээн авдаг болгов
    TouristCampDTO updateCamp(Long id, TouristCampDTO dto, MultipartFile image, User user);

    // Устгах
    void deleteCamp(Long id);

    // Админ болон Модератор хэсэг
    List<TouristCampDTO> getAllApprovedCamps();
    List<TouristCampDTO> getAllPendingCamps();

    // Баталгаажуулах функц
    void approveCamp(Long campId);

    // Модерац
    TouristCampDTO moderateCamp(Long id, boolean approve);

    List<TouristCampDTO> getApprovedCampsBySoum(Long soumId);
}