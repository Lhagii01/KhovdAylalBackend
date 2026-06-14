package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {
    List<CommentReply> findByComment_IdOrderByCreatedAtAsc(Long commentId);
    List<CommentReply> findByBrandComment_IdOrderByCreatedAtAsc(Long brandCommentId);
    void deleteByComment_Id(Long commentId);
}
