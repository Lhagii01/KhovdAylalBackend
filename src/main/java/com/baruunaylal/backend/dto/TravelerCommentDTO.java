package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelerCommentDTO {
    private String source;
    private Long id;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
    @Builder.Default
    private List<ReplyDTO> replies = new ArrayList<>();
    private TravelerUserDTO user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TravelerUserDTO {
        private Long id;
        private String firstName;
        private String username;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyDTO {
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private TravelerUserDTO user;
    }
}
