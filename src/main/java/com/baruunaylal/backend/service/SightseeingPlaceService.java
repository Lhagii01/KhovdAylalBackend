package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.SightseeingPlaceDTO;
import com.baruunaylal.backend.entity.SightseeingPlace;
import com.baruunaylal.backend.entity.Soum;
import com.baruunaylal.backend.repository.SightseeingPlaceRepository;
import com.baruunaylal.backend.repository.SoumRepository; // 🔴 Шинэ: SoumRepository-г import хийв
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SightseeingPlace-тэй холбоотой бизнесийн логик, ялангуяа баталгаажуулалтын
 * (Approval/Moderation) процессыг хариуцна.
 */
@Service
@RequiredArgsConstructor
public class SightseeingPlaceService {

    private final SightseeingPlaceRepository placeRepository;
    private final SoumRepository soumRepository; // 🔴 Шинэ: SoumRepository-г зарлав


    // Utility: Entity-г DTO болгох
    private SightseeingPlaceDTO mapToDTO(SightseeingPlace place) {
        // Энд Soum-ын мэдээллийг авахдаа LazyLoading-ийн асуудал үүсэхгүй байх үүднээс
        // Transactional context дотор эсвэл fetch хийсэн байх ёстой.
        // Энэ код зөв ажиллахын тулд Soum-ийн мэдээллийг Entity-гээс авна.
        return SightseeingPlaceDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .description(place.getDescription())
                .type(place.getType())
                .location(place.getLocation())
                .imageUrl(place.getImageUrl())
                .videoUrl(place.getVideoUrl())
                .hasTouristCamp(place.isHasTouristCamp())
                .soumId(place.getSoum().getId())
                .soumName(place.getSoum().getName())
                .isApproved(place.isApproved())
                .build();
    }

    /**
     * EDITOR: Шинэ үзэсгэлэнт газар нэмэх. Баталгаажуулалт хүлээхээр үүснэ (isApproved=false).
     */
    @Transactional
    public SightseeingPlaceDTO createPlace(SightseeingPlaceDTO dto) {
        Soum soum = soumRepository.findById(dto.getSoumId())
                .orElseThrow(() -> new EntityNotFoundException("Сум олдсонгүй: " + dto.getSoumId()));

        SightseeingPlace place = SightseeingPlace.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .type(dto.getType())
                .location(dto.getLocation())
                .imageUrl(dto.getImageUrl())
                .videoUrl(dto.getVideoUrl())
                .hasTouristCamp(dto.isHasTouristCamp())
                .soum(soum)
                .isApproved(dto.isApproved()) // Allow admin-created places to be visible immediately when payload sets isApproved=true
                .build();

        SightseeingPlace savedPlace = placeRepository.save(place);
        return mapToDTO(savedPlace);
    }

    /**
     * EDITOR/ADMIN: Газрын мэдээллийг шинэчлэх. (Зөвхөн батлагдсан/өөрөө оруулсан мэдээллийг засах боломжтой байх ёстой)
     * Шинэчлэгдсэн мэдээлэл дахин баталгаажуулалт шаардаж болно.
     */
    @Transactional
    public SightseeingPlaceDTO updatePlace(Long id, SightseeingPlaceDTO dto) {
        SightseeingPlace place = placeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Газар олдсонгүй: " + id));

        Soum soum = soumRepository.findById(dto.getSoumId())
                .orElseThrow(() -> new EntityNotFoundException("Сум олдсонгүй: " + dto.getSoumId()));

        place.setName(dto.getName());
        place.setDescription(dto.getDescription());
        place.setType(dto.getType());
        place.setLocation(dto.getLocation());
        place.setImageUrl(dto.getImageUrl());
        place.setVideoUrl(dto.getVideoUrl());
        place.setHasTouristCamp(dto.isHasTouristCamp());
        place.setSoum(soum);

        // Шинэчилсэн мэдээллийг дахин баталгаажуулалт руу оруулах (Админы шийдвэрээс хамаарна)
        // Бид энд түр зуур isApproved-г хэвээр үлдээе, гэхдээ жинхэнэ системд
        // 'isApproved=false' болгож болох юм.

        SightseeingPlace updatedPlace = placeRepository.save(place);
        return mapToDTO(updatedPlace);
    }


    /**
     * USER/Public: Бүх батлагдсан үзэсгэлэнт газруудыг авах.
     */
    public List<SightseeingPlaceDTO> getAllApprovedPlaces() {
        return placeRepository.findAllByIsApproved(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * USER/Public: Тухайн сумын батлагдсан үзэсгэлэнт газруудыг авах.
     */
    public List<SightseeingPlaceDTO> getApprovedPlacesBySoum(Long soumId) {
        return placeRepository.findAllBySoumIdAndIsApproved(soumId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ADMIN: Тухайн сумын бүх үзэсгэлэнт газруудыг статус харгалзахгүйгээр авах.
     */
    public List<SightseeingPlaceDTO> getAllPlacesBySoum(Long soumId) {
        return placeRepository.findAllBySoumId(soumId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ADMIN: Батлагдаагүй (Pending) үзэсгэлэнт газруудыг авах.
     */
    public List<SightseeingPlaceDTO> getAllPendingPlaces() {
        return placeRepository.findAllByIsApproved(false).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ADMIN: Үзэсгэлэнт газрыг баталгаажуулах (approve) эсвэл цуцлах (reject).
     */
    @Transactional
    public SightseeingPlaceDTO moderatePlace(Long id, boolean approve) {
        SightseeingPlace place = placeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Газар олдсонгүй: " + id));

        place.setApproved(approve);
        SightseeingPlace updatedPlace = placeRepository.save(place);

        return mapToDTO(updatedPlace);
    }

    /**
     * ADMIN: Газрыг устгах.
     */
    public void deletePlace(Long id) {
        placeRepository.deleteById(id);
    }
}