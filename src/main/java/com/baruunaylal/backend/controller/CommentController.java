package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.entity.Comment;
import com.baruunaylal.backend.entity.CommentReply;
import com.baruunaylal.backend.dto.TravelerCommentDTO;
import com.baruunaylal.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Frontend-ийн хаягаа баталгаажуулаарай
public class CommentController {

    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    /**
     * Сэтгэгдэл болон зургуудыг хамтад нь хадгалах (Multipart Form Data)
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addComment(
            @RequestPart("comment") Comment comment,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            logger.info("Шинэ сэтгэгдэл ирлээ: CampID={}, Content={}",
                    comment.getCamp() != null ? comment.getCamp().getId() : "null",
                    comment.getContent());

            if (images != null) {
                logger.info("Хавсаргасан зургийн тоо: {}", images.size());
            }

            Comment savedComment = commentService.saveComment(comment, images);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            logger.error("Сэтгэгдэл хадгалах явцад алдаа гарлаа: ", e);
            return ResponseEntity.internalServerError().body("Серверийн алдаа: " + e.getMessage());
        }
    }

    /**
     * Зөвхөн текстээр сэтгэгдэл хадгалах (JSON)
     */
    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCommentJson(@RequestBody Comment comment) {
        try {
            Comment savedComment = commentService.saveComment(comment, null);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            logger.error("JSON сэтгэгдэл хадгалахад алдаа гарлаа: ", e);
            return ResponseEntity.internalServerError().body("Алдаа: " + e.getMessage());
        }
    }

    /**
     * Тухайн баазын бүх сэтгэгдлийг авах
     */
    @GetMapping("/camp/{campId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long campId) {
        return ResponseEntity.ok(commentService.getCommentsByCampId(campId));
    }

    /**
     * Аялагчдад зориулсан DTO хэлбэрээр сэтгэгдлүүдийг авах
     */
    @GetMapping("/camp/{campId}/traveler")
    public ResponseEntity<List<TravelerCommentDTO>> getTravelerComments(@PathVariable Long campId) {
        logger.info("CampID={} дээрх аялагчдын сэтгэгдлийг татаж байна.", campId);
        return ResponseEntity.ok(commentService.getTravelerCommentsByCampId(campId));
    }
    @PostMapping(value = "/{commentId}/replies", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addReply(@PathVariable Long commentId, @RequestBody CommentReply reply) {
        try {
            return ResponseEntity.ok(commentService.saveReply(commentId, reply));
        } catch (Exception e) {
            logger.error("Сэтгэгдлийн хариу хадгалахад алдаа гарлаа: ", e);
            return ResponseEntity.internalServerError().body("Хариу хадгалахад алдаа гарлаа: " + e.getMessage());
        }
    }
    @PutMapping(value = "/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody Comment comment,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(commentService.updateComment(commentId, comment, authentication.getName()));
        } catch (Exception e) {
            logger.error("Сэтгэгдэл засахад алдаа гарлаа: ", e);
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            commentService.deleteComment(commentId, authentication.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Сэтгэгдэл устгахад алдаа гарлаа: ", e);
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
