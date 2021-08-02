package dev.ragnarok.fenrir.model;

import android.os.Parcel;

public abstract class Owner extends AbsModel {

    @OwnerType
    private final int ownerType;

    protected Owner(int ownerType) {
        this.ownerType = ownerType;
    }

    public Owner(Parcel in) {
        super(in);
        //noinspection ResourceType
        ownerType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(ownerType);
    }

    @OwnerType
    public int getOwnerType() {
        return ownerType;
    }

    public int getOwnerId() {
        throw new UnsupportedOperationException();
    }

    public String getDomain() {
        throw new UnsupportedOperationException();
    }

    public String getMaxSquareAvatar() {
        throw new UnsupportedOperationException();
    }

    public String getOriginalAvatar() {
        throw new UnsupportedOperationException();
    }

    public String get100photoOrSmaller() {
        throw new UnsupportedOperationException();
    }

    public String getFullName() {
        throw new UnsupportedOperationException();
    }

    public boolean isVerified() {
        throw new UnsupportedOperationException();
    }

    public boolean isDonated() {
        throw new UnsupportedOperationException();
    }
}
