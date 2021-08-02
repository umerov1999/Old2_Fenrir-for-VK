package dev.ragnarok.fenrir.db.model.entity.feedback;

import dev.ragnarok.fenrir.db.model.entity.Entity;
import dev.ragnarok.fenrir.db.model.entity.EntityWrapper;

/**
 * Base class for types [mention]
 */
public class MentionEntity extends FeedbackEntity {

    private EntityWrapper where = EntityWrapper.empty();

    public MentionEntity(int type) {
        super(type);
    }

    public Entity getWhere() {
        return where.get();
    }

    public MentionEntity setWhere(Entity where) {
        this.where = new EntityWrapper(where);
        return this;
    }
}