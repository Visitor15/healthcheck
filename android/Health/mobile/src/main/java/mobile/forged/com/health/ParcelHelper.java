package mobile.forged.com.health;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/**
 * A utility class that contains methods for converting maps to bundles and
 * back, for use in parceling objects (as Parcel.writeMap() can result in
 * mysterious type errors, while Parcel.writeBundle() is type safe.
 */
public class ParcelHelper {
    /**
     * Converts a Map<String, Parcelable> to a Bundle.
     *
     * @param map
     * @return A Bundle containing the same key-value pairs as the map.
     */
    public static Bundle toBundleParcelable(
            Map<String, ? extends Parcelable> map) {
        Bundle bundle = new Bundle();
        if (map != null) {
            for (String key : map.keySet()) {
                bundle.putParcelable(key, map.get(key));
            }
        }
        return bundle;
    }

    /**
     * Converts a Map<String, ArrayList<Parcelable>> to a Bundle.
     *
     * @param map
     * @return A Bundle containing the same key-value pairs as the map.
     */
    public static Bundle toBundleParcelableArrayList(
            Map<String, ArrayList<? extends Parcelable>> map) {
        Bundle bundle = new Bundle();
        if (map != null) {
            for (String key : map.keySet()) {
                bundle.putParcelableArrayList(key, map.get(key));
            }
        }
        return bundle;
    }

    /**
     * Converts a Map<String, String> to a Bundle.
     *
     * @param map
     * @return A Bundle containing the same key-value pairs as the map.
     */
    public static Bundle toBundleString(Map<String, String> map) {
        Bundle bundle = new Bundle();
        if (map != null) {
            for (String key : map.keySet()) {
                bundle.putString(key, map.get(key));
            }
        }
        return bundle;
    }

    /**
     * Converts a Bundle to a HashMap<String, Parcelable>.
     *
     * @param bundle
     * @param c
     * @return A Map containing the same key-value pairs as the bundle.
     */
    public static <T extends Parcelable> HashMap<String, T> fromBundleParcelable(
            Bundle bundle, Class<T> c) {
        HashMap<String, T> map = new HashMap<String, T>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                map.put(key, c.cast(bundle.getParcelable(key)));
            }
        }
        return map;
    }

    /**
     * Converts a Bundle to a HashMap<String, ArrayList<Parcelable>>.
     *
     * @param bundle
     * @param c
     * @return A Map containing the same key-value pairs as the bundle.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> HashMap<String, ArrayList<T>> fromBundleParcelableArrayList(
            Bundle bundle, Class<T> c) {
        HashMap<String, ArrayList<T>> map = new HashMap<String, ArrayList<T>>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                map.put(key, (ArrayList<T>) bundle.getParcelableArrayList(key));
            }
        }
        return map;
    }

    /**
     * Converts a Bundle to a Map<String, String>.
     *
     * @param bundle
     * @return A Map containing the same key-value pairs as the bundle.
     */
    public static HashMap<String, String> fromBundleString(
            Bundle bundle) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                map.put(key,bundle.getString(key));
            }
        }
        return map;
    }

    /**
     * Writes the boolean to the Parcel in the form of a byte.
     *
     * @param bool
     * @param dest
     */
    public static void writeBoolean(boolean bool, Parcel dest) {
        dest.writeByte((byte) (bool ? 1 : 0));
    }

    /**
     * Reads a byte from the Parcel, returning the boolean equivalent.
     *
     * @param in
     * @return
     */
    public static boolean readBoolean(Parcel in) {
        return in.readByte() != 0;
    }

    /**
     * Writes the Date to the Parcel in the form of a long.
     *
     * @param date
     * @param dest
     */
    public static void writeDate(Date date, Parcel dest) {
        dest.writeLong(date != null ? date.getTime() : -1);
    }

    /**
     * Reads a long from the Parcel, returning the Date equivalent.
     *
     * @param in
     * @return
     */
    public static Date readDate(Parcel in) {
        long date = in.readLong();
        return date == -1 ? null : new Date(date);
    }

    /**
     * Writes the Set to the Parcel in the form of a TypedList.
     *
     * @param set
     * @param dest
     */
    public static <T extends Parcelable> void writeSet(Set<T> set, Parcel dest) {
        dest.writeTypedList(new ArrayList<T>(set));
    }

    /**
     * Reads a TypedList from the Parcel, returning the HashSet equivalent.
     *
     * @param c
     * @param in
     * @return
     */
    public static <T extends Parcelable> HashSet<T> readSet(Creator<T> c, Parcel in) {
        ArrayList<T> list = new ArrayList<T>();
        in.readTypedList(list, c);
        return new HashSet<T>(list);
    }

    /**
     * Writes the Enum to the Parcel in the form of an int.
     *
     * @param e
     * @param dest
     */
    public static <T extends Enum<T>> void writeEnum(Enum<T> e, Parcel dest) {
        dest.writeValue(e != null ? e.ordinal() : -1);
    }

    /**
     * Reads an int from the Parcel and returns the equivalent Enum value.
     *
     * @param e
     * @param in
     * @return
     */
    public static <T extends Enum<T>> T readEnum(Class<T> e, Parcel in) {
        int ordinal = (Integer) in.readValue(Integer.class.getClassLoader());
        return ordinal == -1 ? null : e.getEnumConstants()[ordinal];
    }
}
