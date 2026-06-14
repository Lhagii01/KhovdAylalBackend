package com.baruunaylal.backend.impl;

import com.baruunaylal.backend.dto.TouristSpotCreateRequestDTO;
import com.baruunaylal.backend.dto.TouristSpotDTO;
import com.baruunaylal.backend.entity.MediaItem;
import com.baruunaylal.backend.entity.TouristSpot;
import com.baruunaylal.backend.entity.Category;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import com.baruunaylal.backend.repository.TouristSpotRepository;
import com.baruunaylal.backend.repository.CategoryRepository;
import com.baruunaylal.backend.repository.MediaItemRepository;
import com.baruunaylal.backend.service.TouristSpotService;

import jakarta.persistence.EntityManager; // 🛑 Нэмсэн
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TouristSpotServiceImpl implements TouristSpotService {

    private final TouristSpotRepository touristSpotRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final MediaItemRepository mediaItemRepository;
    private final EntityManager entityManager; // 🛑 Үүнийг нэмж inject хийсэн

    @Override
    public TouristSpotDTO createTouristSpot(TouristSpotCreateRequestDTO requestDTO) {
        TouristSpot touristSpot = new TouristSpot();
        modelMapper.map(requestDTO, touristSpot);

        Set<MediaItem> mediaItems = requestDTO.getMediaItems().stream()
                .map(mediaRequestDTO -> {
                    // POST дээр ModelMapper-ээр мап хийж байна.
                    MediaItem mediaItem = modelMapper.map(mediaRequestDTO, MediaItem.class);
                    mediaItem.setTouristSpot(touristSpot);
                    return mediaItem;
                })
                .collect(Collectors.toSet());

        touristSpot.setMediaItems(mediaItems);

        if (requestDTO.getCategoryIds() != null && !requestDTO.getCategoryIds().isEmpty()) {
            List<Category> categoriesList = categoryRepository.findAllById(requestDTO.getCategoryIds());
            touristSpot.setCategories(new HashSet<>(categoriesList));
        }

        touristSpot.setIsApproved(false);
        TouristSpot savedSpot = touristSpotRepository.save(touristSpot);

        // POST дээр ч гэсэн MediaItem-уудыг хадгалахыг баталгаажуулах
        mediaItemRepository.saveAll(mediaItems);

        return modelMapper.map(savedSpot, TouristSpotDTO.class);
    }

    @Override
    public TouristSpotDTO updateTouristSpot(Long id, TouristSpotCreateRequestDTO requestDTO) {
        TouristSpot existingSpot = touristSpotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TouristSpot", "id", id));

        // 1. Үндсэн талбарууд, Category-г шинэчлэх
        modelMapper.map(requestDTO, existingSpot);
        existingSpot.getCategories().clear();
        if (requestDTO.getCategoryIds() != null && !requestDTO.getCategoryIds().isEmpty()) {
            List<Category> categoriesList = categoryRepository.findAllById(requestDTO.getCategoryIds());
            existingSpot.setCategories(new HashSet<>(categoriesList));
        }

        // 2. ✅ MediaItem-уудыг шинэчлэх

        // 2.1. Хуучин медиаг Database-ээс шууд устгах
        if (existingSpot.getMediaItems() != null && !existingSpot.getMediaItems().isEmpty()) {
            mediaItemRepository.deleteAll(existingSpot.getMediaItems());

            // 🛑 ЗАСВАР: DELETE үйлдлийг Database-д албадаж илгээх (ID NULL алдааг шийдэх)
            entityManager.flush();

            existingSpot.getMediaItems().clear(); // Collection-ийг цэвэрлэх
        }

        // 2.2. Шинэ медиаг үүсгэх (Set-ээр)
        Set<MediaItem> newMediaItems = requestDTO.getMediaItems().stream()
                .map(mediaRequestDTO -> {
                    // DTO-г MediaItem Entity рүү map хийх
                    MediaItem mediaItem = new MediaItem();
                    mediaItem.setUrl(mediaRequestDTO.getUrl());
                    // 🛑 DTO.getType() нь FileType Enum тул, нэрийг String болгон хадгална.
                    mediaItem.setFileType(mediaRequestDTO.getType().name());
                    mediaItem.setTouristSpot(existingSpot);
                    return mediaItem;
                })
                .collect(Collectors.toSet());

        // 2.3. Шинэ медиаг Collection-д нэмэх
        existingSpot.getMediaItems().addAll(newMediaItems);


        // 3. TouristSpot-ийг хадгалах (Энэ нь MediaItem-уудын ID-г авахыг баталгаажуулна)
        TouristSpot updatedSpot = touristSpotRepository.save(existingSpot);

        // 4. MediaItem-уудыг тусад нь saveAll хийж, TouristSpot-ийн ID-г оноохыг тулгах
        mediaItemRepository.saveAll(newMediaItems);

        return modelMapper.map(updatedSpot, TouristSpotDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristSpotDTO> getAllApprovedSpots() {
        return touristSpotRepository.findAll().stream()
                .filter(TouristSpot::getIsApproved)
                .map(spot -> modelMapper.map(spot, TouristSpotDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TouristSpotDTO getTouristSpotById(Long id) {
        TouristSpot touristSpot = touristSpotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TouristSpot", "id", id));
        return modelMapper.map(touristSpot, TouristSpotDTO.class);
    }

    @Override
    public void deleteTouristSpot(Long id) {
        if (!touristSpotRepository.existsById(id)) {
            throw new ResourceNotFoundException("TouristSpot", "id", id);
        }
        touristSpotRepository.deleteById(id);
    }

    // ✅ Компиляцын алдааг зассан method
    @Override
    public TouristSpotDTO updateApprovalStatus(Long id, boolean isApproved) {
        TouristSpot existingSpot = touristSpotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TouristSpot", "id", id));

        existingSpot.setIsApproved(isApproved);
        TouristSpot updatedSpot = touristSpotRepository.save(existingSpot);

        return modelMapper.map(updatedSpot, TouristSpotDTO.class);
    }
}