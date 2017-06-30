package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import edu.umbc.ebiquity.mithril.MithrilAC;

public class SemanticLocation extends SemanticUserContext implements
        Parcelable,
        Comparable<SemanticLocation> {
    public static final Creator<SemanticLocation> CREATOR = new Creator<SemanticLocation>() {
        @Override
        public SemanticLocation createFromParcel(Parcel in) {
            return new SemanticLocation(in);
        }

        @Override
        public SemanticLocation[] newArray(int size) {
            return new SemanticLocation[size];
        }
    };
    private final String type = MithrilAC.getPrefKeyContextTypeLocation();
    private Location location;
    private Address address = new Address(Locale.getDefault());
    private String inferredLocation;
    private boolean enabled = false;
    private String name;
    private String placeId;
    private List<Integer> placeTypes;
    private boolean geofenced = false;
    private int level;

    protected SemanticLocation(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        inferredLocation = in.readString();
        enabled = in.readByte() != 0;
        name = in.readString();
        placeId = in.readString();
        geofenced = in.readByte() != 0;
        level = in.readInt();
    }

    public SemanticLocation(Location location, Address address, String inferredLocation, boolean enabled, String name, String placeId, List<Integer> placeTypes, boolean geofenced, int level) {
        this.location = location;
        this.address = address;
        this.inferredLocation = inferredLocation;
        this.enabled = enabled;
        this.name = name;
        this.placeId = placeId;
        this.placeTypes = placeTypes;
        this.geofenced = geofenced;
        this.level = level;
    }

    public SemanticLocation(String inferredLocation, Location location, int level) {
        this.location = location;
        this.inferredLocation = inferredLocation;
        this.level = level;
    }

    public SemanticLocation(String inferredLocation, Location location, String name, String placeId, List<Integer> placeTypes, int level) {
        this.location = location;
        this.inferredLocation = inferredLocation;
        this.name = name;
        this.placeId = placeId;
        this.placeTypes = placeTypes;
        this.level = level;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeParcelable(address, flags);
        dest.writeString(inferredLocation);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(name);
        dest.writeString(placeId);
        dest.writeByte((byte) (geofenced ? 1 : 0));
        dest.writeInt(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredLocation;
    }

    @Override
    public void setLabel(String label) {
        inferredLocation = label;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getInferredLocation() {
        return inferredLocation;
    }

    public void setInferredLocation(String inferredLocation) {
        this.inferredLocation = inferredLocation;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public boolean isGeofenced() {
        return geofenced;
    }

    public void setGeofenced(boolean geofenced) {
        this.geofenced = geofenced;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int compareTo(@NonNull SemanticLocation o) {
        return Comparators.NAME.compare(this, o);
    }

    public int comparePlaceIds(@NonNull SemanticLocation o) {
        return Comparators.PLACE.compare(this, o);
    }

    public int compareLevels(@NonNull SemanticLocation o) {
        return Comparators.LEVEL.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticLocation)) return false;

        SemanticLocation that = (SemanticLocation) o;

        if (!getName().equals(that.getName())) return false;
        return getPlaceId().equals(that.getPlaceId());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getPlaceId().hashCode();
        return result;
    }

    public static class Comparators {

        public static Comparator<SemanticLocation> NAME = new Comparator<SemanticLocation>() {
            @Override
            public int compare(SemanticLocation o1, SemanticLocation o2) {
                return o1.name.compareTo(o2.name);
            }
        };
        public static Comparator<SemanticLocation> LEVEL = new Comparator<SemanticLocation>() {
            @Override
            public int compare(SemanticLocation o1, SemanticLocation o2) {
                return o1.level - o2.level;
            }
        };
        public static Comparator<SemanticLocation> PLACE = new Comparator<SemanticLocation>() {
            @Override
            public int compare(SemanticLocation o1, SemanticLocation o2) {
                return o1.placeId.compareTo(o2.placeId);
            }
        };
    }
}