package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.entity.Tour;
import com.baruunaylal.backend.dto.TourResponseDTO;
import com.baruunaylal.backend.service.TourService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tours")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TourController {

    private final TourService tourService;
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<List<TourResponseDTO>> getTours(
            @RequestParam(required = false) Long campId,
            @RequestParam(defaultValue = "mn") String lang) {
        return ResponseEntity.ok(tourService.getToursMapped(campId, lang));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTourById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "mn") String lang) {
        return ResponseEntity.ok(tourService.getTourDtoById(id, lang));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourResponseDTO> saveTour(
            @RequestParam("titleMn") String titleMn,
            @RequestParam("descMn") String descMn,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "priceAdult", required = false) Double priceAdult,
            @RequestParam("duration") String duration,
            @RequestParam("campId") Long campId,
            @RequestParam("tourType") String tourType,
            @RequestParam(value = "tip", required = false) String tip,
            @RequestParam(value = "included", required = false) String included,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "galleryImages", required = false) List<MultipartFile> galleryImages,
            @RequestParam(value = "encoded", defaultValue = "false") boolean encoded,
            @RequestParam("itineraries") String itinerariesJson) throws IOException {

        if (encoded) {
            titleMn = decodeUtf8(titleMn);
            descMn = decodeUtf8(descMn);
            duration = decodeUtf8(duration);
            tourType = decodeUtf8(tourType);
            tip = decodeUtf8(tip);
            included = decodeUtf8(included);
            itinerariesJson = decodeUtf8(itinerariesJson);
        }

        List<TourResponseDTO.ItineraryDTO> itineraryList = mapper.readValue(itinerariesJson,
                new TypeReference<List<TourResponseDTO.ItineraryDTO>>() {});

        Double finalPrice = priceAdult != null ? priceAdult : price;
        Tour savedTour = tourService.saveTour(titleMn, descMn, finalPrice, duration, campId,
                tourType, tip, included, image, galleryImages, itineraryList);
        return ResponseEntity.ok(tourService.getTourDtoById(savedTour.getId(), "mn"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TourResponseDTO> saveTourJson(@RequestBody TourRequest request) throws IOException {
        String included = normalizeIncluded(request.getIncluded());
        Double finalPrice = request.getPriceAdult() != null ? request.getPriceAdult() : request.getPrice();
        Tour savedTour = tourService.saveTour(request.getTitleMn(), request.getDescMn(), finalPrice,
                request.getDuration(), request.getCampId(), request.getTourType(), request.getTip(),
                included, null, null, request.getItineraries());
        return ResponseEntity.ok(tourService.getTourDtoById(savedTour.getId(), "mn"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourResponseDTO> updateTour(
            @PathVariable Long id,
            @RequestParam("titleMn") String titleMn,
            @RequestParam("descMn") String descMn,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "priceAdult", required = false) Double priceAdult,
            @RequestParam("duration") String duration,
            @RequestParam("campId") Long campId,
            @RequestParam("tourType") String tourType,
            @RequestParam(value = "tip", required = false) String tip,
            @RequestParam(value = "included", required = false) String included,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "galleryImages", required = false) List<MultipartFile> galleryImages,
            @RequestParam(value = "encoded", defaultValue = "false") boolean encoded,
            @RequestParam("itineraries") String itinerariesJson) throws IOException {

        if (encoded) {
            titleMn = decodeUtf8(titleMn);
            descMn = decodeUtf8(descMn);
            duration = decodeUtf8(duration);
            tourType = decodeUtf8(tourType);
            tip = decodeUtf8(tip);
            included = decodeUtf8(included);
            itinerariesJson = decodeUtf8(itinerariesJson);
        }

        List<TourResponseDTO.ItineraryDTO> itineraryList = mapper.readValue(itinerariesJson,
                new TypeReference<List<TourResponseDTO.ItineraryDTO>>() {});

        Double finalPrice = priceAdult != null ? priceAdult : price;
        Tour updatedTour = tourService.updateTour(id, titleMn, descMn, finalPrice, duration, campId,
                tourType, tip, included, image, galleryImages, itineraryList);
        return ResponseEntity.ok(tourService.getTourDtoById(updatedTour.getId(), "mn"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TourResponseDTO> updateTourJson(
            @PathVariable Long id,
            @RequestBody TourRequest request) throws IOException {
        String included = normalizeIncluded(request.getIncluded());
        Double finalPrice = request.getPriceAdult() != null ? request.getPriceAdult() : request.getPrice();
        Tour updatedTour = tourService.updateTour(id, request.getTitleMn(), request.getDescMn(), finalPrice,
                request.getDuration(), request.getCampId(), request.getTourType(), request.getTip(),
                included, null, null, request.getItineraries());
        return ResponseEntity.ok(tourService.getTourDtoById(updatedTour.getId(), "mn"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String normalizeIncluded(Object included) throws IOException {
        if (included == null) return null;
        return included instanceof String ? (String) included : mapper.writeValueAsString(included);
    }

    private String decodeUtf8(String value) {
        if (value == null) return null;
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    @Data
    public static class TourRequest {
        private String titleMn;
        private String descMn;
        private Double price;
        private Double priceAdult;
        private Double priceChild;
        private String duration;
        private Long campId;
        private String tourType;
        private String tip;
        private Object included;
        private List<TourResponseDTO.ItineraryDTO> itineraries;
    }
}
