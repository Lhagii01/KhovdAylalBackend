package com.baruunaylal.backend.service;


import com.baruunaylal.backend.dto.TouristSpotCreateRequestDTO;
import com.baruunaylal.backend.dto.TouristSpotDTO;
import java.util.List;

/**
 * Аялал жуулчлалын газрын үйлчилгээний logic-ийг тодорхойлох interface.
 */
public interface TouristSpotService {

    // Шинэ TouristSpot үүсгэх
    TouristSpotDTO createTouristSpot(TouristSpotCreateRequestDTO requestDTO);

    // Бүх баталгаажсан TouristSpot-уудыг авах
    List<TouristSpotDTO> getAllApprovedSpots();

    // ID-гаар TouristSpot-ийг авах
    TouristSpotDTO getTouristSpotById(Long id);

    // TouristSpot-ийг шинэчлэх
    TouristSpotDTO updateTouristSpot(Long id, TouristSpotCreateRequestDTO requestDTO);

    // TouristSpot-ийг устгах
    void deleteTouristSpot(Long id);

    // ✅ НЭГДҮГЭЭР ШАЛТГААН: Гарын үсэг энд тодорхойлогдсон байна.
    // TouristSpot-ийн баталгаажуулалтын статусыг өөрчлөх
    TouristSpotDTO updateApprovalStatus(Long id, boolean isApproved);
}