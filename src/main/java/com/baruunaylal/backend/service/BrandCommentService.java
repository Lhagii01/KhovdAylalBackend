package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.TravelerCommentDTO;
import com.baruunaylal.backend.entity.BrandComment;
import com.baruunaylal.backend.entity.BrandProduct;
import com.baruunaylal.backend.entity.CommentReply;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.repository.BrandCommentRepository;
import com.baruunaylal.backend.repository.BrandProductRepository;
import com.baruunaylal.backend.repository.CommentReplyRepository;
import com.baruunaylal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandCommentService {
    private final BrandCommentRepository brandCommentRepository;
    private final BrandProductRepository brandProductRepository;
    private final CommentReplyRepository commentReplyRepository;
    private final UserRepository userRepository;
    private final String uploadDir = "uploads/brand-comments/";

    public BrandComment saveComment(Long productId, BrandComment comment, List<MultipartFile> images) throws IOException {
        BrandProduct product = brandProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Брэнд бүтээгдэхүүн олдсонгүй."));

        if (comment.getUser() == null || comment.getUser().getId() == null) {
            throw new IllegalArgumentException("Хэрэглэгчийн мэдээлэл дутуу байна.");
        }

        User user = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

        comment.setProduct(product);
        comment.setUser(user);

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID() + "_" + (originalFileName != null ? originalFileName.replace(" ", "_") : "image");
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imageUrls.add("/uploads/brand-comments/" + fileName);
                }
            }
        }

        comment.setImageUrls(imageUrls);
        return brandCommentRepository.save(comment);
    }

    public List<TravelerCommentDTO> getProductComments(Long productId) {
        return brandCommentRepository.findByProduct_IdOrderByCreatedAtDesc(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TravelerCommentDTO toDto(BrandComment comment) {
        User user = comment.getUser();
        return TravelerCommentDTO.builder()
                .source("brand")
                .id(comment.getId())
                .content(comment.getContent())
                .rating(comment.getRating())
                .createdAt(comment.getCreatedAt())
                .imageUrls(comment.getImageUrls() != null ? comment.getImageUrls() : new ArrayList<>())
                .replies(toReplyDtos(commentReplyRepository.findByBrandComment_IdOrderByCreatedAtAsc(comment.getId())))
                .user(TravelerCommentDTO.TravelerUserDTO.builder()
                        .id(user != null ? user.getId() : null)
                        .firstName(user != null ? user.getFirstName() : null)
                        .username(user != null ? user.getEmail() : "Хэрэглэгч")
                        .build())
                .build();
    }

    public CommentReply saveReply(Long commentId, CommentReply reply) {
        if (reply.getUser() == null || reply.getUser().getId() == null) {
            throw new RuntimeException("Хэрэглэгч тодорхойгүй байна.");
        }

        BrandComment comment = brandCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Сэтгэгдэл олдсонгүй."));
        User user = userRepository.findById(reply.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

        reply.setBrandComment(comment);
        reply.setComment(null);
        reply.setUser(user);
        return commentReplyRepository.save(reply);
    }

    private List<TravelerCommentDTO.ReplyDTO> toReplyDtos(List<CommentReply> replies) {
        if (replies == null) return new ArrayList<>();
        return replies.stream()
                .map(reply -> {
                    User user = reply.getUser();
                    return TravelerCommentDTO.ReplyDTO.builder()
                            .id(reply.getId())
                            .content(reply.getContent())
                            .createdAt(reply.getCreatedAt())
                            .user(TravelerCommentDTO.TravelerUserDTO.builder()
                                    .id(user != null ? user.getId() : null)
                                    .firstName(user != null ? user.getFirstName() : null)
                                    .username(user != null ? user.getUsername() : null)
                                    .build())
                            .build();
                })
                .toList();
    }
}
