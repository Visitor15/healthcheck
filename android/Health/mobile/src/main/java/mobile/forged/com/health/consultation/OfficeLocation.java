package mobile.forged.com.health.consultation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by visitor15 on 11/12/14.
 */
public class OfficeLocation implements Parcelable
{
    // [region] constants

    // [endregion]


    // [region] instance variables

    // TODO: Change to GeoLocation object

    public double latitude;

    public double longitude;

    private final String _streetAddress1;

    private final String _streetAddress2;

    private final String _city;

    private final String _state;

    private final String _zipCode;

    private final String _country;


    // [endregion]


    // [region] constructors

    public OfficeLocation(double latitude, double longitude, String streetAddress1,
                          String streetAddress2, String city, String state, String zipCode, String country)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        _streetAddress1 = streetAddress1;
        _streetAddress2 = streetAddress2;
        _city = city;
        _state = state;
        _zipCode = zipCode;
        _country = country;
    }

    public OfficeLocation (Parcel in)
    {
        latitude = in.readDouble();
        longitude = in.readDouble();
        _streetAddress1 = in.readString();
        _streetAddress2 = in.readString();
        _city = in.readString();
        _state = in.readString();;
        _zipCode = in.readString();
        _country = in.readString();
    }

    // [endregion]


    // [region] properties

    public String getAddress()
    {
        return String.format("%s %s %s, %s %s %s", _streetAddress1, _streetAddress2,
                _city, _state, _country, _zipCode);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(_streetAddress1);
        dest.writeString(_streetAddress2);
        dest.writeString(_city);
        dest.writeString(_state);
        dest.writeString(_zipCode);
        dest.writeString(_country);
    }

    // [endregion]

    // [region] getters/setters

    public String getAddress1()
    {
        return _streetAddress1;
    }

    public String getAddress2()
    {
        return _streetAddress2;
    }

    public String getCity()
    {
        return _city;
    }

    public String getState()
    {
        return _state;
    }

    public String getZipCode()
    {
        return _zipCode;
    }

    public boolean setLongLatFromAddress(Context context)
    {
        Geocoder coder = new Geocoder(context);
        List<Address> addresses;

        try {
            addresses = coder.getFromLocationName(this.getAddress(),1);
            if (addresses == null || addresses.size() == 0) {
                return false;
            }
            Address address = addresses.get(0); // note: assume first one is the one we want
            this.latitude = address.getLatitude();
            this.longitude = address.getLongitude();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // [endregion]


    // [region] helper methods

    public static final Parcelable.Creator<OfficeLocation> CREATOR =
            new Parcelable.Creator<OfficeLocation>()
            {
                @Override
                public OfficeLocation createFromParcel(Parcel in)
                {
                    return new OfficeLocation(in);
                }

                @Override
                public OfficeLocation[] newArray(int size)
                {
                    return new OfficeLocation[size];
                }
            };
    // [endregion]

}