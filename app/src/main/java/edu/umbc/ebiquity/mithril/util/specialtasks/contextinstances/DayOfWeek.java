package edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances;

import android.os.Parcel;
import android.os.Parcelable;

public enum DayOfWeek implements Parcelable {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<DayOfWeek> CREATOR = new Creator<DayOfWeek>() {
        @Override
        public DayOfWeek createFromParcel(final Parcel source) {
            return DayOfWeek.values()[source.readInt()];
        }

        @Override
        public DayOfWeek[] newArray(final int size) {
            return new DayOfWeek[size];
        }
    };
}