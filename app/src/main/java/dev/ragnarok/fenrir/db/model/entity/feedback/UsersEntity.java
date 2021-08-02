package dev.ragnarok.fenrir.db.model.entity.feedback;

public class UsersEntity extends FeedbackEntity {

    private int[] ids;

    public UsersEntity(int type) {
        super(type);
    }

    public int[] getOwners() {
        return ids;
    }

    public UsersEntity setOwners(int[] ids) {
        this.ids = ids;
        return this;
    }
}