package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.RoomResponseDto;
import com.baruunaylal.backend.entity.Room;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.RoomRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class RoomController {

    private final RoomRepository roomRepository;
    private final TouristCampRepository touristCampRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/camp/{campId}")
    public ResponseEntity<?> getRoomsByCamp(@PathVariable Long campId) {
        try {
            List<Room> rooms = roomRepository.findByCamp_Id(campId);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("Өрөө татахад алдаа гарлаа: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Сервер дээр алдаа гарлаа");
        }
    }

    @GetMapping("/camp/{campId}/public")
    public ResponseEntity<List<RoomResponseDto>> getPublicRoomsByCamp(@PathVariable Long campId) {
        List<Room> rooms = roomRepository.findByCamp_Id(campId);
        List<RoomResponseDto> response = rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.getCapacity(),
                        room.getIsAvailable(),
                        room.getDescription()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/camp/{campId}/available")
    public ResponseEntity<List<RoomResponseDto>> getAvailableRoomsByCamp(
            @PathVariable Long campId,
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {
        List<Room> rooms = roomRepository.findByCamp_Id(campId);
        List<RoomResponseDto> response = rooms.stream()
                .filter(room -> Boolean.TRUE.equals(room.getIsAvailable()))
                .filter(room -> bookingRepository
                        .findOverlappingBookingsForRoom(room.getId(), checkInDate, checkOutDate)
                        .isEmpty())
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getPrice(),
                        room.getCapacity(),
                        room.getIsAvailable(),
                        room.getDescription()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN', 'ROLE_ADMIN', 'ROLE_CAMP_ADMIN')")
    @PostMapping("/{campId}")
    public ResponseEntity<?> addRoom(@PathVariable Long campId, @RequestBody Room room) {
        // 'incompatible types' алдааг orElseGet ашиглан засав
        return touristCampRepository.findById(campId).map(camp -> {
            room.setCamp(camp);
            room.setId(null);
            if (room.getIsAvailable() == null) room.setIsAvailable(true);

            Room savedRoom = roomRepository.save(room);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN', 'CAMP_MANAGER', 'ROLE_ADMIN', 'ROLE_CAMP_ADMIN', 'ROLE_CAMP_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        return roomRepository.findById(id).map(room -> {
            room.setRoomNumber(roomDetails.getRoomNumber());
            room.setRoomType(roomDetails.getRoomType());
            room.setPrice(roomDetails.getPrice());
            room.setCapacity(roomDetails.getCapacity());
            room.setDescription(roomDetails.getDescription());
            if (roomDetails.getIsAvailable() != null) room.setIsAvailable(roomDetails.getIsAvailable());

            return ResponseEntity.ok(roomRepository.save(room));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN', 'ROLE_ADMIN', 'ROLE_CAMP_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
