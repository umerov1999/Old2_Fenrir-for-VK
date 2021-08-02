package dev.ragnarok.fenrir.db.model.entity;

public class NotSupportedEntity extends Entity {
    private String type;
    private String body;

    public String getType() {
        return type;
    }

    public NotSupportedEntity setType(String type) {
        this.type = type;
        return this;
    }

    public String getBody() {
        return body;
    }

    public NotSupportedEntity setBody(String body) {
        this.body = body;
        return this;
    }
}
