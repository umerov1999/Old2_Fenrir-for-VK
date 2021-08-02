package dev.ragnarok.fenrir.db.model.entity;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class EntitiesWrapper implements Iterable<Entity> {

    public static final EntitiesWrapper EMPTY = new EntitiesWrapper(Collections.emptyList());
    private final List<Entity> entities;

    public EntitiesWrapper(List<Entity> entities) {
        this.entities = entities;
    }

    public static EntitiesWrapper wrap(List<Entity> entities) {
        return entities == null ? EMPTY : new EntitiesWrapper(entities);
    }

    public List<Entity> get() {
        return entities;
    }

    @NonNull
    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }
}