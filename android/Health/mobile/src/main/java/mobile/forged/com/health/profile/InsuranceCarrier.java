package mobile.forged.com.health.profile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by visitor15 on 11/9/14.
 */
public class InsuranceCarrier implements Parcelable
{
    public String identifier;
    public String name;

    public InsuranceCarrier(String identifier, String name)
    {
        this.identifier = identifier;
        this.name = name;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(identifier);
        dest.writeString(name);
    }

    public InsuranceCarrier(Parcel in)
    {
        identifier = in.readString();
        name = in.readString();
    }

    public static final Parcelable.Creator<InsuranceCarrier> CREATOR =
            new Parcelable.Creator<InsuranceCarrier>()
            {
                @Override
                public InsuranceCarrier createFromParcel(final Parcel in)
                {
                    return new InsuranceCarrier(in);
                }

                @Override
                public InsuranceCarrier[] newArray(final int size)
                {
                    return new InsuranceCarrier[size];
                }
            };

}
