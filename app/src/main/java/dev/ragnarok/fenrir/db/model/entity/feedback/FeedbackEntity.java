package dev.ragnarok.fenrir.db.model.entity.feedback;

import dev.ragnarok.fenrir.db.model.entity.CommentEntity;
import dev.ragnarok.fenrir.model.feedback.FeedbackType;

public class FeedbackEntity {

    private final @FeedbackType
    int type;

    private long date;

    private CommentEntity reply;

    public FeedbackEntity(@FeedbackType int type) {
        this.type = type;
    }

    public @FeedbackType
    int getType() {
        return type;
    }

    public long getDate() {
        return date;
    }

    public FeedbackEntity setDate(long date) {
        this.date = date;
        return this;
    }

    public CommentEntity getReply() {
        return reply;
    }

    public FeedbackEntity setReply(CommentEntity reply) {
        this.reply = reply;
        return this;
    }
}