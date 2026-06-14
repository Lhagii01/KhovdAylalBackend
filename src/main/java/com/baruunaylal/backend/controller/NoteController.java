package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.NoteRequestDto;
import com.baruunaylal.backend.dto.NoteResponseDto;
import com.baruunaylal.backend.dto.NoteStatsDto;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.entity.Note;
import com.baruunaylal.backend.entity.TouristCamp;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.NoteRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import com.baruunaylal.backend.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/api/v1/notes")
    @RequiredArgsConstructor
    @CrossOrigin(origins = "*")
    public class NoteController {

        private static final String TYPE_TRAVEL = "TRAVEL";
        private static final String TYPE_CAMP_LOG = "CAMP_LOG";
        private static final String TYPE_SYSTEM_NOTE = "SYSTEM_NOTE";

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final TouristCampRepository campRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/my-travel")
    public ResponseEntity<List<NoteResponseDto>> getMyTravelNotes(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(noteRepository
                .findByUserIdAndTypeOrderByNoteDateDescCreatedAtDesc(user.getId(), TYPE_TRAVEL)
                .stream()
                .map(this::toDto)
                .toList());
    }

    @PostMapping("/my-travel")
    public ResponseEntity<?> createTravelNote(@RequestBody NoteRequestDto request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Booking booking = null;
        TouristCamp camp = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId()).orElse(null);
            if (booking == null || !isBookingOwner(booking, user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Энэ захиалгад тэмдэглэл бичих эрхгүй байна."));
            }
            camp = resolveCamp(booking);
        }

        Note note = Note.builder()
                .user(user)
                .booking(booking)
                .camp(camp)
                .type(TYPE_TRAVEL)
                .title(cleanTitle(request.getTitle()))
                .content(request.getContent())
                .noteDate(request.getNoteDate() != null ? request.getNoteDate() : LocalDate.now())
                .imageUrls(toJson(request.getImageUrls()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(noteRepository.save(note)));
    }

    @GetMapping("/camp-log")
    public ResponseEntity<List<NoteResponseDto>> getCampLogs(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        TouristCamp camp = findManagedCamp(user);
        if (camp == null) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(noteRepository
                .findByCampIdAndTypeOrderByNoteDateDescCreatedAtDesc(camp.getId(), TYPE_CAMP_LOG)
                .stream()
                .map(this::toDto)
                .toList());
    }

    @PostMapping("/camp-log")
    public ResponseEntity<?> createCampLog(@RequestBody NoteRequestDto request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        TouristCamp camp = findManagedCamp(user);
        if (camp == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Танд холбогдсон бааз олдсонгүй."));
        }

        Note note = Note.builder()
                .user(user)
                .camp(camp)
                .type(TYPE_CAMP_LOG)
                .title(cleanTitle(request.getTitle()))
                .content(request.getContent())
                .noteDate(request.getNoteDate() != null ? request.getNoteDate() : LocalDate.now())
                .imageUrls(toJson(request.getImageUrls()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(noteRepository.save(note)));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<NoteStatsDto> getNoteStats(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!isSystemAdmin(user)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        long travelNotes = noteRepository.countByType(TYPE_TRAVEL);
        long campLogs = noteRepository.countByType(TYPE_CAMP_LOG);
        NoteStatsDto stats = NoteStatsDto.builder()
                .totalNotes(noteRepository.count())
                .travelNotes(travelNotes)
                .campLogs(campLogs)
                .systemNotes(noteRepository.countByType(TYPE_SYSTEM_NOTE))
                .activeWriters(noteRepository.countDistinctUsersByType(TYPE_TRAVEL))
                .build();

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/my-notes")
    public ResponseEntity<List<NoteResponseDto>> getMySystemNotes(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!isSystemAdmin(user)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(noteRepository
                .findByUserIdAndTypeOrderByNoteDateDescCreatedAtDesc(user.getId(), TYPE_SYSTEM_NOTE)
                .stream()
                .map(this::toDto)
                .toList());
    }

    @PostMapping("/admin/my-notes")
    public ResponseEntity<?> createSystemNote(@RequestBody NoteRequestDto request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!isSystemAdmin(user)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Note note = Note.builder()
                .user(user)
                .type(TYPE_SYSTEM_NOTE)
                .title(cleanTitle(request.getTitle()))
                .content(request.getContent())
                .noteDate(request.getNoteDate() != null ? request.getNoteDate() : LocalDate.now())
                .imageUrls("[]")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(noteRepository.save(note)));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long noteId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Note note = noteRepository.findById(noteId).orElse(null);
        if (note == null) return ResponseEntity.notFound().build();

        boolean ownsNote = note.getUser() != null && note.getUser().getId().equals(user.getId());
        boolean campOwner = TYPE_CAMP_LOG.equals(note.getType()) && user.getCampId() != null
                && note.getCamp() != null && user.getCampId().equals(note.getCamp().getId());

        if (!ownsNote && !campOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        noteRepository.delete(note);
        return ResponseEntity.ok(Map.of("message", "Тэмдэглэл устгагдлаа."));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null) return null;
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    private boolean isSystemAdmin(User user) {
        String role = user.getRole() != null ? user.getRole().name() : "";
        return role.equals("ADMIN") || role.equals("SYSTEM_ADMIN");
    }

    private boolean isBookingOwner(Booking booking, User user) {
        if (booking.getUser() != null && booking.getUser().getId().equals(user.getId())) return true;
        if (user.getPhone() == null || booking.getPhoneNumber() == null) return false;
        return normalizePhone(user.getPhone()).equals(normalizePhone(booking.getPhoneNumber()));
    }

    private TouristCamp resolveCamp(Booking booking) {
        if (booking.getCamp() != null) return booking.getCamp();
        if (booking.getRoom() != null && booking.getRoom().getCamp() != null) return booking.getRoom().getCamp();
        return null;
    }

    private TouristCamp findManagedCamp(User user) {
        if (user.getCampId() != null) {
            TouristCamp camp = campRepository.findById(user.getCampId()).orElse(null);
            if (camp != null) return camp;
        }

        List<TouristCamp> byAdmin = campRepository.findByAdminId(user.getId());
        if (!byAdmin.isEmpty()) return byAdmin.get(0);

        return campRepository.findByOwnerId(user.getId()).orElse(null);
    }

    private String cleanTitle(String title) {
        return title == null || title.trim().isEmpty() ? "Тэмдэглэл" : title.trim();
    }

    private String normalizePhone(String input) {
        return input == null ? "" : input.replace(" ", "").replace("-", "").replace("+", "");
    }

    private String toJson(List<String> urls) {
        try {
            return objectMapper.writeValueAsString(urls != null ? urls : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private NoteResponseDto toDto(Note note) {
        TouristCamp camp = note.getCamp();
        Booking booking = note.getBooking();
        if (camp == null && booking != null) camp = resolveCamp(booking);

        String userName = "Хэрэглэгч";
        if (note.getUser() != null) {
            String first = note.getUser().getFirstName() != null ? note.getUser().getFirstName() : "";
            String last = note.getUser().getLastName() != null ? note.getUser().getLastName() : "";
            String fullName = (first + " " + last).trim();
            userName = fullName.isEmpty() ? note.getUser().getEmail() : fullName;
        }

        return NoteResponseDto.builder()
                .id(note.getId())
                .userId(note.getUser() != null ? note.getUser().getId() : null)
                .userName(userName)
                .bookingId(booking != null ? booking.getId() : null)
                .campId(camp != null ? camp.getId() : null)
                .campName(camp != null ? camp.getName() : null)
                .type(note.getType())
                .title(note.getTitle())
                .content(note.getContent())
                .noteDate(note.getNoteDate())
                .imageUrls(fromJson(note.getImageUrls()))
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
