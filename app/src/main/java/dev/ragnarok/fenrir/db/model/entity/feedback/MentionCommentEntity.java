package dev.ragnarok.fenrir.db.model.entity.feedback;

import dev.ragnarok.fenrir.db.model.entity.CommentEntity;
import dev.ragnarok.fenrir.db.model.entity.Entity;
import dev.ragnarok.fenrir.db.model.entity.EntityWrapper;

/**
 * Base class for types [mention_comments, mention_comment_photo, mention_comment_video]
 */
public class MentionCommentEntity extends FeedbackEntity {

    private CommentEntity where;

    private EntityWrapper commented = EntityWrapper.empty();

    public MentionCommentEntity(int type) {
        super(type);
    }

    public CommentEntity getWhere() {
        return where;
    }

    public MentionCommentEntity setWhere(CommentEntity where) {
        this.where = where;
        return this;
    }

    public Entity getCommented() {
        return commented.get();
    }

    public MentionCommentEntity setCommented(Entity commented) {
        this.commented = new EntityWrapper(commented);
        return this;
    }
}