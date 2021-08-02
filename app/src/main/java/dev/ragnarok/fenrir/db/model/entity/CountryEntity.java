package dev.ragnarok.fenrir.db.model.entity;


public class CountryEntity extends Entity {

    private final int id;

    private final String title;

    public CountryEntity(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}