package dev.ragnarok.fenrir.db.column;

import android.provider.BaseColumns;

public final class AttachmentsColumns implements BaseColumns {

    public static final String TABLENAME = "attachments";
    public static final String MESSAGE_ID = "message_id";
    public static final String TYPE = "type";
    public static final String DATA = "data";
    public static final String FULL_ID = TABLENAME + "." + _ID;
    public static final String FULL_MESSAGE_ID = TABLENAME + "." + MESSAGE_ID;
    public static final String FULL_TYPE = TABLENAME + "." + TYPE;
    public static final String FULL_DATA = TABLENAME + "." + DATA;

    private AttachmentsColumns() {
    }
}