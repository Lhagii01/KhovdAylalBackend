package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.ContactMessageRequestDto;
import com.baruunaylal.backend.entity.ContactMessage;
import com.baruunaylal.backend.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactMessageController {

    private final ContactMessageRepository contactMessageRepository;

    @PostMapping("/contact-messages")
    public ResponseEntity<?> sendContactMessage(@Valid @RequestBody ContactMessageRequestDto request) {
        ContactMessage message = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .message(request.getMessage())
                .read(false)
                .build();

        contactMessageRepository.save(message);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Мессеж амжилттай хадгалагдлаа.");
    }

    @GetMapping("/admin/messages")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        List<ContactMessage> messages = contactMessageRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/admin/messages/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount() {
        long unreadCount = contactMessageRepository.countByReadFalse();
        return ResponseEntity.ok(unreadCount);
    }

    @PutMapping("/admin/messages/{id}/read")
    public ResponseEntity<?> markMessageRead(@PathVariable Long id) {
        return contactMessageRepository.findById(id)
                .map(message -> {
                    message.setRead(true);
                    contactMessageRepository.save(message);
                    return ResponseEntity.ok("Мессеж уншсан гэж тэмдэглэгдлээ.");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Мессеж олдсонгүй."));
    }
}
