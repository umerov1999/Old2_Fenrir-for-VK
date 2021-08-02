package dev.ragnarok.fenrir.db.model.entity;

public class EventEntity extends Entity {

    private final int id;
    private String button_text;
    private String text;

    public EventEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public EventEntity setText(String text) {
        this.text = text;
        return this;
    }

    public String getButton_text() {
        return button_text;
    }

    public EventEntity setButton_text(String button_text) {
        this.button_text = button_text;
        return this;
    }
}
