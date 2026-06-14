package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.TourResponseDTO;
import com.baruunaylal.backend.entity.*;
import com.baruunaylal.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {

    private final TourRepository tourRepository;
    private final TranslationRepository translationRepository;
    private final TourItineraryRepository itineraryRepository;
    private final TourImageRepository tourImageRepository;

    private final String uploadDir = "uploads/tours/";

    public List<TourResponseDTO> getToursMapped(Long campId, String lang) {
        List<Tour> tours = (campId != null) ? tourRepository.findByCampId(campId) : tourRepository.findAll();
        return tours.stream().map(tour -> convertToDto(tour, lang)).collect(Collectors.toList());
    }

    public TourResponseDTO getTourDtoById(Long id, String lang) {
        Tour tour = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Аялал олдсонгүй"));
        return convertToDto(tour, lang);
    }

    private TourResponseDTO convertToDto(Tour tour, String lang) {
        String title = getTranslation(tour.getId(), lang, "title", tour.getTitle());
        String description = getTranslation(tour.getId(), lang, "description", tour.getDescription());
        String tip = getTranslation(tour.getId(), lang, "tip", tour.getTip());
        String included = getTranslation(tour.getId(), lang, "included", tour.getIncluded());

        return TourResponseDTO.builder()
                .id(tour.getId())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .imageUrl(tour.getImageUrl())
                .campId(tour.getCampId())
                .tourType(tour.getTourType())
                .title(title)
                .description(description)
                .tip(tip)
                .included(included)
                .itineraries(itineraryRepository.findByTourId(tour.getId()).stream()
                        .map(i -> TourResponseDTO.ItineraryDTO.builder()
                                .dayNumber(i.getDayNumber())
                                .title(i.getTitle())
                                .content(i.getContent())
                                .build())
                        .collect(Collectors.toList()))
                .gallery(tourImageRepository.findByTourId(tour.getId()).stream()
                        .map(TourImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public Tour saveTour(String titleMn, String descMn, Double price, String duration, Long campId,
                         String tourType, String tip, String included,
                         MultipartFile mainImage, List<MultipartFile> galleryImages,
                         List<TourResponseDTO.ItineraryDTO> itineraries) throws IOException {

        Tour tour = new Tour();
        setTourData(tour, titleMn, descMn, price, duration, campId, tourType, tip, included);

        if (mainImage != null && !mainImage.isEmpty()) {
            tour.setImageUrl("/" + uploadDir + saveImage(mainImage));
        }

        Tour savedTour = tourRepository.save(tour);

        if (galleryImages != null && !galleryImages.isEmpty()) {
            for (MultipartFile file : galleryImages) {
                saveSingleGalleryImage(savedTour, file);
            }
        }

        saveAllTranslations(savedTour.getId(), titleMn, descMn, tip, included);
        updateItineraries(savedTour.getId(), itineraries);
        return savedTour;
    }

    @Transactional
    public Tour updateTour(Long id, String titleMn, String descMn, Double price, String duration, Long campId,
                           String tourType, String tip, String included,
                           MultipartFile mainImage, List<MultipartFile> galleryImages,
                           List<TourResponseDTO.ItineraryDTO> itineraries) throws IOException {

        Tour tour = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Аялал олдсонгүй"));
        setTourData(tour, titleMn, descMn, price, duration, campId, tourType, tip, included);

        if (mainImage != null && !mainImage.isEmpty()) {
            tour.setImageUrl("/" + uploadDir + saveImage(mainImage));
        }

        if (galleryImages != null && !galleryImages.isEmpty()) {
            tourImageRepository.deleteByTourId(id);
            for (MultipartFile file : galleryImages) {
                saveSingleGalleryImage(tour, file);
            }
        }

        saveAllTranslations(id, titleMn, descMn, tip, included);
        updateItineraries(id, itineraries);
        return tourRepository.save(tour);
    }

    private void setTourData(Tour tour, String title, String desc, Double price, String duration,
                             Long campId, String type, String tip, String included) {
        tour.setTitle(title);
        tour.setDescription(desc);
        tour.setPrice(price != null ? price : 0.0);
        tour.setDuration(duration);
        tour.setCampId(campId);
        tour.setTourType(type);
        tour.setTip(tip);
        tour.setIncluded(included);
    }

    private void saveAllTranslations(Long entityId, String title, String desc, String tip, String included) {
        saveTranslation("tour", entityId, "mn", "title", title);
        saveTranslation("tour", entityId, "mn", "description", desc);
        saveTranslation("tour", entityId, "mn", "tip", tip);
        saveTranslation("tour", entityId, "mn", "included", included);
    }

    private String getTranslation(Long entityId, String lang, String fieldName, String defaultValue) {
        return translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
                        "tour", entityId, lang, fieldName)
                .map(Translation::getContent)
                .filter(c -> c != null && !c.trim().isEmpty())
                .orElse(defaultValue);
    }

    private void updateItineraries(Long tourId, List<TourResponseDTO.ItineraryDTO> itineraries) {
        itineraryRepository.deleteByTourId(tourId);
        if (itineraries != null && !itineraries.isEmpty()) {
            for (TourResponseDTO.ItineraryDTO dto : itineraries) {
                if ((dto.getTitle() == null || dto.getTitle().isEmpty()) &&
                        (dto.getContent() == null || dto.getContent().isEmpty())) continue;

                TourItinerary item = new TourItinerary();
                item.setTourId(tourId);
                item.setDayNumber(dto.getDayNumber() != null ? dto.getDayNumber() : 1);
                item.setTitle(dto.getTitle());
                item.setContent(dto.getContent());
                itineraryRepository.save(item);
            }
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path directoryPath = Paths.get(uploadDir);
        if (!Files.exists(directoryPath)) Files.createDirectories(directoryPath);
        Path filePath = directoryPath.resolve(imageName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return imageName;
    }

    private void saveSingleGalleryImage(Tour tour, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            TourImage tourImage = TourImage.builder()
                    .imageUrl("/" + uploadDir + saveImage(file))
                    .tour(tour)
                    .build();
            tourImageRepository.save(tourImage);
        }
    }

    private void saveTranslation(String type, Long entityId, String lang, String field, String content) {
        Translation t = translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(type, entityId, lang, field)
                .orElse(new Translation());
        t.setEntityType(type);
        t.setEntityId(entityId);
        t.setLanguageCode(lang);
        t.setFieldName(field);
        t.setContent(content);
        translationRepository.save(t);
    }

    @Transactional
    public void deleteById(Long id) {
        itineraryRepository.deleteByTourId(id);
        tourImageRepository.deleteByTourId(id);
        translationRepository.deleteByEntityTypeAndEntityId("tour", id);
        tourRepository.deleteById(id);
    }
}
