package dev.ragnarok.fenrir.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.atomic.AtomicInteger;

public class AttachmenEntry implements Parcelable {

    public static final Creator<AttachmenEntry> CREATOR = new Creator<AttachmenEntry>() {
        @Override
        public AttachmenEntry createFromParcel(Parcel in) {
            return new AttachmenEntry(in);
        }

        @Override
        public AttachmenEntry[] newArray(int size) {
            return new AttachmenEntry[size];
        }
    };
    private static final AtomicInteger ID_GEN = new AtomicInteger();
    private final int id;
    private final AbsModel attachment;
    private int optionalId;
    private boolean canDelete;
    private boolean accompanying;

    public AttachmenEntry(boolean canDelete, AbsModel attachment) {
        this.canDelete = canDelete;
        this.attachment = attachment;
        id = ID_GEN.incrementAndGet();
    }

    protected AttachmenEntry(Parcel in) {
        id = in.readInt();
        if (id > ID_GEN.intValue()) {
            ID_GEN.set(id);
        }

        optionalId = in.readInt();
        canDelete = in.readByte() != 0;
        accompanying = in.readByte() != 0;

        ParcelableModelWrapper wrapper = in.readParcelable(ParcelableModelWrapper.class.getClassLoader());
        attachment = wrapper.get();
    }

    public int getId() {
        return id;
    }

    public AbsModel getAttachment() {
        return attachment;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public AttachmenEntry setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        return this;
    }

    public boolean isAccompanying() {
        return accompanying;
    }

    public AttachmenEntry setAccompanying(boolean accompanying) {
        this.accompanying = accompanying;
        return this;
    }

    public int getOptionalId() {
        return optionalId;
    }

    public AttachmenEntry setOptionalId(int optionalId) {
        this.optionalId = optionalId;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(optionalId);
        dest.writeByte((byte) (canDelete ? 1 : 0));
        dest.writeByte((byte) (accompanying ? 1 : 0));

        ParcelableModelWrapper wrapper = ParcelableModelWrapper.wrap(attachment);
        dest.writeParcelable(wrapper, flags);
    }
}
