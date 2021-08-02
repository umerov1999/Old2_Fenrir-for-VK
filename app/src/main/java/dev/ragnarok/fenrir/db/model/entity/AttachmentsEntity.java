package dev.ragnarok.fenrir.db.model.entity;

import java.util.List;


public class AttachmentsEntity {

    private final List<Entity> entities;

    public AttachmentsEntity(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}