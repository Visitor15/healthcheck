package mobile.forged.com.health.profile;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import mobile.forged.com.health.ParcelHelper;

/**
 * Created by visitor15 on 11/9/14.
 */
public class Medication implements Parcelable, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // [region] constants

    public static final int FIELD_ID = 0;
    public static final int FIELD_NAME = 1;

    // [end region]

    // [region] instance variables

    public final String id;
    public final String name;

    private String mEntNo;
    private boolean mChecked;

    // [endregion]

    // [region] constructors

    public Medication(String id, String name)
    {
        this.id = id;
        this.name = name;
        this.mChecked = false;
    }

    // [endregion]

    public String getName()
    {
        return name;
    }

    public String getEntNo()
    {
        return mEntNo;
    }

    public boolean isChecked()
    {
        return mChecked;
    }

    public void setChecked(Boolean check)
    {
        this.mChecked = check;
    }

    // [region] overridden methods

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && ((Medication)obj).id.equals(id)
                && ((Medication)obj).name.equals(name))
        {
            return true;
        }

        return false;
    }

    // [endregion]

    // [region] parcelable

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(mEntNo);
        ParcelHelper.writeBoolean(mChecked, dest);
    }

    public Medication(Parcel in)
    {
        id = (String)in.readValue(String.class.getClassLoader());
        name = (String)in.readValue(String.class.getClassLoader());
        mEntNo = (String)in.readValue(String.class.getClassLoader());
        mChecked = ParcelHelper.readBoolean(in);
    }

    public static final Parcelable.Creator<Medication> CREATOR =
            new Parcelable.Creator<Medication>()
            {
                @Override
                public Medication createFromParcel(Parcel in)
                {
                    return new Medication(in);
                }

                @Override
                public Medication[] newArray(int size)
                {
                    return new Medication[size];
                }
            };

    @Override
    public int describeContents()
    {
        return 0;
    }

    // [endregion]
}