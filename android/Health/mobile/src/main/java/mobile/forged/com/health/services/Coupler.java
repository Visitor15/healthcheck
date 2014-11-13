package mobile.forged.com.health.services;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Coupler implements Parcelable, Serializable
{
    private static final long serialVersionUID = -2324706501668128628L;

    public int couplerNo;
    public String name;
    public String sName;
    public String rev;
    public int prog;

    public Coupler(int couplerNo, String name, String sName, String rev, int prog)
    {
        this.couplerNo = couplerNo;
        this.name = name;
        this.sName = sName;
        this.rev = rev;
        this.prog = prog;
    }

    @Override
    public String toString()
    {
        return name;
    }

    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(couplerNo);
        dest.writeValue(name);
        dest.writeValue(sName);
        dest.writeValue(rev);
        dest.writeValue(prog);
    }

    private Coupler(Parcel in) {
        couplerNo = (Integer) in.readValue(Integer.class.getClassLoader());
        name = (String) in.readValue(String.class.getClassLoader());
        sName = (String) in.readValue(String.class.getClassLoader());
        rev = (String) in.readValue(String.class.getClassLoader());
        prog = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Coupler> CREATOR = new Parcelable.Creator<Coupler>() {
        @Override
        public Coupler createFromParcel(Parcel source) {
            return new Coupler(source);
        }

        @Override
        public Coupler[] newArray(int size) {
            return new Coupler[size];
        }
    };

    // [endregion]
}