package com.baruunaylal.backend.impl;

import com.baruunaylal.backend.dto.HotelDTO;
import com.baruunaylal.backend.dto.HotelRegisterRequestDTO;
import com.baruunaylal.backend.entity.Soum;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.entity.Hotel;
import com.baruunaylal.backend.repository.HotelRepository;
import com.baruunaylal.backend.repository.SoumRepository;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.service.HotelService;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final SoumRepository soumRepository;
    private final UserRepository userRepository;

    /**
     * Entity-г DTO руу хөрвүүлэх (Description-ийг нэмсэн)
     */
    private HotelDTO convertToDTO(Hotel hotel) {
        if (hotel == null) return null;
        return HotelDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription()) // Нэмэгдсэн
                .address(hotel.getLocation())
                .isApproved(hotel.isApproved())
                .soumId(hotel.getSoum() != null ? hotel.getSoum().getId() : null)
                .soumName(hotel.getSoum() != null ? hotel.getSoum().getName() : null)
                .build();
    }

    /**
     * Шинэ зочид буудал бүртгэх (Owner-ийг автоматаар онооно)
     */
    @Override
    @Transactional
    public HotelDTO createHotel(HotelRegisterRequestDTO request) {
        Soum soum = soumRepository.findById(request.getSoumId())
                .orElseThrow(() -> new ResourceNotFoundException("Сум олдсонгүй."));

        // 1. Одоо нэвтэрсэн байгаа хэрэглэгчийн Email-ийг SecurityContext-оос авах
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч олдсонгүй."));

        // 2. GeneralInfo-оос тайлбарыг авах
        String desc = "";
        if (request.getGeneralInfo() != null) {
            desc = request.getGeneralInfo().getDescription();
        }

        // 3. Entity үүсгэх
        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .location(request.getAddress())
                .description(desc)
                .soum(soum)
                .owner(owner) // Эзэмшигчийг оноож байна 🟢
                .isApproved(false)
                .build();

        return convertToDTO(hotelRepository.save(hotel));
    }

    @Override
    public List<HotelDTO> getHotelsByOwner(Long ownerId) {
        return hotelRepository.findAllByOwnerId(ownerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelDTO> getAllApprovedHotels() {
        return hotelRepository.findAllByIsApproved(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Буудал олдсонгүй."));
        return convertToDTO(hotel);
    }

    @Override
    @Transactional
    public HotelDTO updateHotel(Long id, HotelDTO hotelDTO) {
        Hotel existingHotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Буудал олдсонгүй."));

        if (hotelDTO.getName() != null) existingHotel.setName(hotelDTO.getName());
        if (hotelDTO.getAddress() != null) existingHotel.setLocation(hotelDTO.getAddress());
        if (hotelDTO.getDescription() != null) existingHotel.setDescription(hotelDTO.getDescription());

        return convertToDTO(hotelRepository.save(existingHotel));
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Буудал олдсонгүй.");
        }
        hotelRepository.deleteById(id);
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelDTO updateHotelApproval(Long id, Boolean isApproved) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Буудал олдсонгүй."));
        hotel.setApproved(isApproved);
        return convertToDTO(hotelRepository.save(hotel));
    }
}