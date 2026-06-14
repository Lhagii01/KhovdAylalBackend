package com.baruunaylal.backend.impl;

import com.baruunaylal.backend.dto.TouristCampDTO;
import com.baruunaylal.backend.entity.*;
import com.baruunaylal.backend.enums.Role;
import com.baruunaylal.backend.repository.*;
import com.baruunaylal.backend.service.TouristCampService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TouristCampServiceImpl implements TouristCampService {

    private final TouristCampRepository campRepository;
    private final SoumRepository soumRepository;
    private final SightseeingPlaceRepository sightseeingPlaceRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRequestRepository refundRequestRepository;
    private final TourRepository tourRepository;
    private final TourImageRepository tourImageRepository;

    private final String UPLOAD_DIR = "uploads/";

    @Override
    @Transactional(readOnly = true)
    public List<TouristCampDTO> getAllApprovedCamps() {
        return campRepository.findByApprovedTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristCampDTO> getAllPendingCamps() {
        return campRepository.findByApprovedFalseOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristCampDTO> getCampsByUser(User user) {
        if (user == null) return Collections.emptyList();
        // ✅ adminId биш ownerId-аар хайх нь илүү найдвартай (Бааз дээр owner_id дататай байгаа)
        return campRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TouristCampDTO createCamp(TouristCampDTO dto, MultipartFile image, User user) {
        Soum soum = soumRepository.findById(dto.getSoumId())
                .orElseThrow(() -> new RuntimeException("Сум олдсонгүй (ID: " + dto.getSoumId() + ")"));

        TouristCamp camp = TouristCamp.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .services(dto.getServices())
                .contact(dto.getContact())
                .establishedYear(dto.getEstablishedYear())
                .staffCount(dto.getStaffCount())
                .routeType(dto.getRouteType())
                .guestCapacity(dto.getGuestCapacity())
                .materialBase(dto.getMaterialBase())
                .permitStatus(dto.getPermitStatus())
                .mapUrl(dto.getMapUrl())
                .discountRules(dto.getDiscountRules())
                .averagePrice(dto.getAveragePrice())
                .soum(soum)
                .owner(user)
                .adminId(user.getId()) // Шинээр үүсэхэд админ ID-г шууд оноож өгнө
                .approved(user.getRole() == Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        if (dto.getPlaceId() != null) {
            camp.setPlace(sightseeingPlaceRepository.findById(dto.getPlaceId()).orElse(null));
        }

        if (image != null && !image.isEmpty()) {
            camp.setImageUrl(saveFile(image));
        }

        return convertToDTO(campRepository.save(camp));
    }

    @Override
    @Transactional
    public void approveCamp(Long campId) {
        TouristCamp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Бааз олдсонгүй"));

        camp.setApproved(true);
        User owner = camp.getOwner();
        if (owner != null) {
            owner.setRole(Role.CAMP_ADMIN);
            owner.setCampId(camp.getId());
            // ✅ ЗАССАН: adminId талбарт owner-ийн ID-г хуулж өгснөөр Dashboard хайлт амжилттай болно
            camp.setAdminId(owner.getId());
            userRepository.save(owner);
        }
        campRepository.save(camp);
    }

    @Override
    @Transactional
    public TouristCampDTO updateCamp(Long id, TouristCampDTO dto, MultipartFile image, User user) {
        TouristCamp camp = campRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бааз олдсонгүй (ID: " + id + ")"));

        boolean isOwner = camp.getOwner() != null && camp.getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Танд энэ баазыг засах эрх байхгүй");
        }

        if (dto.getName() != null) camp.setName(dto.getName());
        if (dto.getDescription() != null) camp.setDescription(dto.getDescription());
        if (dto.getServices() != null) camp.setServices(dto.getServices());
        if (dto.getContact() != null) camp.setContact(dto.getContact());
        if (dto.getEstablishedYear() != null) camp.setEstablishedYear(dto.getEstablishedYear());
        if (dto.getStaffCount() != null) camp.setStaffCount(dto.getStaffCount());
        if (dto.getRouteType() != null) camp.setRouteType(dto.getRouteType());
        if (dto.getGuestCapacity() != null) camp.setGuestCapacity(dto.getGuestCapacity());
        if (dto.getMaterialBase() != null) camp.setMaterialBase(dto.getMaterialBase());
        if (dto.getPermitStatus() != null) camp.setPermitStatus(dto.getPermitStatus());
        if (dto.getMapUrl() != null) camp.setMapUrl(dto.getMapUrl());
        if (dto.getDiscountRules() != null) camp.setDiscountRules(dto.getDiscountRules());
        if (dto.getAveragePrice() != null) camp.setAveragePrice(dto.getAveragePrice());

        if (dto.getSoumId() != null) {
            Soum soum = soumRepository.findById(dto.getSoumId())
                    .orElseThrow(() -> new RuntimeException("Сум олдсонгүй"));
            camp.setSoum(soum);
        }

        if (dto.getPlaceId() != null) {
            camp.setPlace(sightseeingPlaceRepository.findById(dto.getPlaceId()).orElse(null));
        }

        if (image != null && !image.isEmpty()) {
            deleteOldFile(camp.getImageUrl());
            camp.setImageUrl(saveFile(image));
        }

        return convertToDTO(campRepository.save(camp));
    }

    private TouristCampDTO convertToDTO(TouristCamp camp) {
        if (camp == null) return null;

        TouristCampDTO dto = TouristCampDTO.builder()
                .id(camp.getId())
                .name(camp.getName())
                .description(camp.getDescription())
                .services(camp.getServices())
                .serviceDetails(camp.getServices())
                .contact(camp.getContact())
                .establishedYear(camp.getEstablishedYear())
                .staffCount(camp.getStaffCount())
                .routeType(camp.getRouteType())
                .guestCapacity(camp.getGuestCapacity())
                .materialBase(camp.getMaterialBase())
                .permitStatus(camp.getPermitStatus())
                .mapUrl(camp.getMapUrl())
                .discountRules(camp.getDiscountRules())
                .averagePrice(camp.getAveragePrice())
                .imageUrl(camp.getImageUrl())
                .isApproved(camp.isApproved())
                .createdAt(camp.getCreatedAt())
                .adminId(camp.getAdminId())
                .build();

        if (camp.getSoum() != null) {
            dto.setSoumId(camp.getSoum().getId());
            dto.setSoumName(camp.getSoum().getName());
        }

        if (camp.getPlace() != null) {
            dto.setPlaceId(camp.getPlace().getId());
            dto.setPlaceName(camp.getPlace().getName());
        }

        if (camp.getOwner() != null) {
            dto.setOwnerId(camp.getOwner().getId());
            dto.setOwnerFirstName(camp.getOwner().getFirstName());
            dto.setOwnerLastName(camp.getOwner().getLastName());
            dto.setOwnerPhone(camp.getOwner().getPhone());
        }

        return dto;
    }

    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return "/" + UPLOAD_DIR + fileName;
        } catch (IOException e) {
            log.error("File save error: {}", e.getMessage());
            throw new RuntimeException("Файл хадгалахад алдаа гарлаа");
        }
    }

    private void deleteOldFile(String filePath) {
        if (filePath != null && filePath.startsWith("/")) {
            try {
                Path path = Paths.get(filePath.substring(1));
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.error("File delete error: {}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristCampDTO> getAllCamps() {
        return campRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TouristCampDTO getCampById(Long id) {
        return campRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    @Override
    @Transactional
    public void deleteCamp(Long id) {
        TouristCamp camp = campRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бааз олдсонгүй (ID: " + id + ")"));

        List<Booking> bookings = bookingRepository.findAllByCampIdFlexible(id);
        List<Long> bookingIds = bookings.stream().map(Booking::getId).toList();

        if (!bookingIds.isEmpty()) {
            paymentRepository.deleteByBookingIdIn(bookingIds);
            refundRequestRepository.deleteByBookingIdIn(bookingIds);
            noteRepository.deleteByBookingIdIn(bookingIds);
        }

        refundRequestRepository.deleteByCampId(id);
        noteRepository.deleteAll(noteRepository.findByCampId(id));
        commentRepository.deleteAll(commentRepository.findByCampIdOrderByCreatedAtDesc(id));

        bookingRepository.deleteAll(bookings);
        tourImageRepository.deleteByTouristCampId(id);
        tourRepository.deleteAll(tourRepository.findByCampId(id));

        deleteOldFile(camp.getImageUrl());
        campRepository.delete(camp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristCampDTO> getCampsByOwnerId(Long id) {
        // ✅ ЗАССАН: Repository-ийн зөв функцийг дуудаж байна
        return campRepository.findByOwnerIdOrderByCreatedAtDesc(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TouristCampDTO> getApprovedCampsBySoum(Long id) {
        return campRepository.findAllBySoumIdAndApprovedTrue(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TouristCampDTO moderateCamp(Long id, boolean approve) {
        TouristCamp camp = campRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Бааз олдсонгүй"));
        camp.setApproved(approve);

        // Зөвшөөрөх үед adminId-г мөн тохируулж өгвөл сайн
        if (approve && camp.getOwner() != null) {
            camp.setAdminId(camp.getOwner().getId());
        }

        return convertToDTO(campRepository.save(camp));
    }
}
