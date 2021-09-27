package dev.ragnarok.fenrir.db.model.entity;

import java.util.List;


public class CommentsEntity {

    private final List<CommentEntity> comments;

    public CommentsEntity(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public List<CommentEntity> getEntities() {
        return comments;
    }
}
