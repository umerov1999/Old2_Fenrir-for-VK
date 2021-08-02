package dev.ragnarok.fenrir.db.model.entity;


public class EntityWrapper {

    private final Entity entity;

    public EntityWrapper(Entity entity) {
        this.entity = entity;
    }

    public static EntityWrapper empty() {
        return new EntityWrapper(null);
    }

    public Entity get() {
        return entity;
    }
}