package dev.ragnarok.fenrir.db.model;


public class IdPairEntity {

    private final int id;

    private final int ownerId;

    public IdPairEntity(int id, int ownerId) {
        this.id = id;
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getId() {
        return id;
    }
}