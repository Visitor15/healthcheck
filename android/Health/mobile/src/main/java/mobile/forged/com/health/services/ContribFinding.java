package mobile.forged.com.health.services;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by visitor15 on 11/12/14.
 */
public class ContribFinding implements Parcelable, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String name;
    public String value;
    public String note;
    public String linkID;


    public ContribFinding(String name, String value, String note, String linkID)
    {
        super();
        this.name = name;
        this.value = value;
        this.note = note;
        this.linkID = linkID;
    }

    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(value);
        dest.writeValue(note);
        dest.writeValue(linkID);
    }

    private ContribFinding(Parcel in) {
        name = (String) in.readValue(String.class.getClassLoader());
        value = (String) in.readValue(String.class.getClassLoader());
        note = (String) in.readValue(String.class.getClassLoader());
        linkID = (String) in.readValue(String.class.getClassLoader());
    }

    public static final Parcelable.Creator<ContribFinding> CREATOR = new Parcelable.Creator<ContribFinding>() {
        @Override
        public ContribFinding createFromParcel(Parcel source) {
            return new ContribFinding(source);
        }

        @Override
        public ContribFinding[] newArray(int size) {
            return new ContribFinding[size];
        }
    };

    // [endregion]
}