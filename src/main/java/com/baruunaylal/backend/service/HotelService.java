package com.baruunaylal.backend.service;


import com.baruunaylal.backend.dto.HotelDTO;
import com.baruunaylal.backend.dto.HotelRegisterRequestDTO;

import java.util.List;

public interface HotelService {

    // CRUD Operations (Үндсэн функц)
    HotelDTO createHotel(HotelRegisterRequestDTO request);
    HotelDTO getHotelById(Long id);
    List<HotelDTO> getAllApprovedHotels();
    HotelDTO updateHotel(Long id, HotelDTO hotelDTO);
    void deleteHotel(Long id);

    // =========================================================================
    // Admin/Additional Methods (Админ болон нэмэлт функц)
    // ЭНД НЭМЭХ ШААРДЛАГАТАЙ БАЙСАН METHOD-УУД:

    /**
     * Бүх зочид буудлуудыг буцаана (Батлагдсан болон батлагдаагүй).
     * @return Бүх зочид буудлуудын DTO жагсаалт.
     */
    List<HotelDTO> getAllHotels();
    List<HotelDTO> getHotelsByOwner(Long ownerId);
    /**
     * Зочид буудлын баталгаажуулалтын төлөвийг шинэчлэх (Админ үйлдэл).
     * @param id Зочид буудлын ID.
     * @param isApproved Шинэ баталгаажуулалтын төлөв.
     * @return Шинэчлэгдсэн зочид буудлын DTO.
     */
    HotelDTO updateHotelApproval(Long id, Boolean isApproved);
}