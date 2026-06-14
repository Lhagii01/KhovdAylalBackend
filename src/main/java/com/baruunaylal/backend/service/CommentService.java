package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.TravelerCommentDTO;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.entity.Comment;
import com.baruunaylal.backend.entity.CommentReply;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.entity.TouristCamp;
import com.baruunaylal.backend.enums.Role;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.CommentRepository;
import com.baruunaylal.backend.repository.CommentReplyRepository;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TouristCampRepository touristCampRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentReplyRepository commentReplyRepository;

    private final String uploadDir = "uploads/comments/";

    public Comment saveComment(Comment comment, List<MultipartFile> images) throws IOException {
        if (comment.getCamp() == null || comment.getCamp().getId() == null) {
            throw new RuntimeException("Алдаа: Лагерь тодорхойгүй байна!");
        }
        if (comment.getUser() == null || comment.getUser().getId() == null) {
            throw new RuntimeException("Алдаа: Хэрэглэгч тодорхойгүй байна!");
        }

        TouristCamp camp = touristCampRepository.findById(comment.getCamp().getId())
                .orElseThrow(() -> new RuntimeException("Лагерь олдсонгүй"));
        User user = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй"));

        comment.setCamp(camp);
        comment.setUser(user);

        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            for (MultipartFile img : images) {
                if (!img.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + img.getOriginalFilename();
                    Path targetPath = path.resolve(fileName);
                    Files.copy(img.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    imageUrls.add("/uploads/comments/" + fileName);
                }
            }
        }
        comment.setImageUrls(imageUrls);
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByCampId(Long campId) {
        return commentRepository.findAllByCampId(campId);
    }

    public List<TravelerCommentDTO> getTravelerCommentsByCampId(Long campId) {
        List<TravelerCommentDTO> commentItems = commentRepository.findAllByCampId(campId)
                .stream()
                .map(c -> TravelerCommentDTO.builder()
                        .source("comments")
                        .id(c.getId())
                        .content(c.getContent())
                        .rating(c.getRating() == null ? 5 : c.getRating())
                        .createdAt(c.getCreatedAt())
                        .imageUrls(c.getImageUrls() == null ? new ArrayList<>() : c.getImageUrls())
                        .replies(toReplyDtos(commentReplyRepository.findByComment_IdOrderByCreatedAtAsc(c.getId())))
                        .user(TravelerCommentDTO.TravelerUserDTO.builder()
                                .id(c.getUser() != null ? c.getUser().getId() : null)
                                .firstName(c.getUser() != null ? c.getUser().getFirstName() : null)
                                .username(c.getUser() != null ? c.getUser().getUsername() : null)
                                .build())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        List<TravelerCommentDTO> bookingItems = bookingRepository.findBookingsWithCommentByCampId(campId)
                .stream()
                .map(this::bookingToTravelerComment)
                .toList();

        commentItems.addAll(bookingItems);
        commentItems.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        return commentItems;
    }

    private TravelerCommentDTO bookingToTravelerComment(Booking booking) {
        String displayName = booking.getCustomerName();
        if (displayName == null || displayName.isBlank()) {
            if (booking.getUser() != null && booking.getUser().getFirstName() != null && !booking.getUser().getFirstName().isBlank()) {
                displayName = booking.getUser().getFirstName();
            } else if (booking.getUser() != null) {
                displayName = booking.getUser().getUsername();
            } else {
                displayName = "Аялагч";
            }
        }

        return TravelerCommentDTO.builder()
                .source("bookings")
                .id(booking.getId())
                .content(booking.getComment())
                .rating(5)
                .createdAt(booking.getBookingDate())
                .imageUrls(new ArrayList<>())
                .user(TravelerCommentDTO.TravelerUserDTO.builder()
                        .id(booking.getUser() != null ? booking.getUser().getId() : null)
                        .firstName(displayName)
                        .username(booking.getUser() != null ? booking.getUser().getUsername() : null)
                        .build())
                .build();
    }

    public CommentReply saveReply(Long commentId, CommentReply reply) {
        if (reply.getUser() == null || reply.getUser().getId() == null) {
            throw new RuntimeException("Хэрэглэгч тодорхойгүй байна.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Сэтгэгдэл олдсонгүй."));
        User user = userRepository.findById(reply.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

        reply.setComment(comment);
        reply.setBrandComment(null);
        reply.setUser(user);
        return commentReplyRepository.save(reply);
    }

    public Comment updateComment(Long commentId, Comment updatedComment, String currentEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Сэтгэгдэл олдсонгүй."));
        ensureCommentOwnerOrAdmin(comment, currentEmail);

        if (updatedComment.getContent() != null && !updatedComment.getContent().isBlank()) {
            comment.setContent(updatedComment.getContent().trim());
        }
        if (updatedComment.getRating() != null) {
            int rating = Math.max(1, Math.min(5, updatedComment.getRating()));
            comment.setRating(rating);
        }
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String currentEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Сэтгэгдэл олдсонгүй."));
        ensureCommentOwnerOrAdmin(comment, currentEmail);
        commentReplyRepository.deleteByComment_Id(commentId);
        commentRepository.delete(comment);
    }

    private void ensureCommentOwnerOrAdmin(Comment comment, String currentEmail) {
        if (currentEmail == null || currentEmail.isBlank()) {
            throw new AccessDeniedException("Нэвтэрсэн байх шаардлагатай.");
        }

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AccessDeniedException("Хэрэглэгч олдсонгүй."));
        boolean isOwner = comment.getUser() != null && Objects.equals(comment.getUser().getId(), currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN
                || currentUser.getRole() == Role.CAMP_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Энэ сэтгэгдлийг засах/устгах эрхгүй байна.");
        }
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
