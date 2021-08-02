package dev.ragnarok.fenrir.db.model.entity.feedback;

import dev.ragnarok.fenrir.db.model.entity.PostEntity;

public class PostFeedbackEntity extends FeedbackEntity {

    private PostEntity post;

    public PostFeedbackEntity(int type) {
        super(type);
    }

    public PostEntity getPost() {
        return post;
    }

    public PostFeedbackEntity setPost(PostEntity post) {
        this.post = post;
        return this;
    }
}