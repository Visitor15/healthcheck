package mobile.forged.com.health.consultation;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import mobile.forged.com.health.ParcelHelper;
import mobile.forged.com.health.profile.InsurancePlanAndCarrier;
import mobile.forged.com.health.profile.ProfilePhysician;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Physician implements Parcelable
{
    // [region] constants

    // [endregion]

    // [region] instance variables

    public String middleInitial;
    public String specialty;

    public String avatarUrl;
    public String clinic;
    public double distance;
    public String firstName;
    public String physicianID;
    public String lastName;
    public String phoneNumber;
    public String suffix;
    public boolean isSponsor;
    public boolean wasCalled;
    public ArrayList<InsurancePlanAndCarrier> insurancePlans;

    public OfficeLocation officeLocation;

    // [endregion]

    // [region] constructors
    public Physician(final String physicianID, final String firstName,
                     final String lastName, final String middleInitial, final String suffix,
                     final String avatarURL, final String specialty, final String clinic,
                     final OfficeLocation officeLocation, final double distance,
                     final String phoneNumber, final boolean isSponsor,
                     ArrayList<InsurancePlanAndCarrier> insurancePlans)
    {
        this.physicianID = physicianID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.suffix = suffix;
        avatarUrl = avatarURL;
        this.clinic = clinic;
        this.specialty =
                (specialty == null || specialty.equals(""))
                        ? "General Physician"
                        : specialty;
        this.distance = distance;
        this.phoneNumber = phoneNumber;
        this.isSponsor = isSponsor;
        this.officeLocation = officeLocation;
        this.insurancePlans = insurancePlans;
    }

    public Physician(final ProfilePhysician profilePhysician, String suffix)
    {
        this.physicianID = null;
        this.firstName = profilePhysician.getFirstName();
        this.lastName = profilePhysician.getLastName();
        this.middleInitial = null;
        this.suffix = suffix;
        avatarUrl = null;
        this.clinic = "";
        this.specialty = profilePhysician.specialty;
        this.distance = -1;
        this.phoneNumber = profilePhysician.phone;
        this.isSponsor = false;
        this.officeLocation = new OfficeLocation(0, 0,
                profilePhysician.address, "", profilePhysician.city,
                profilePhysician.state, profilePhysician.zip, "");
        this.insurancePlans = null;
    }

    // [endregion]

    // [region] properties

    public String fullName()
    {
        final String middleName =
                (middleInitial == null || middleInitial.isEmpty()) ? " " : String
                        .format(" %s ", middleInitial);
        return suffix == null
                ? String.format("%s%s%s", firstName, middleName, lastName)
                : String.format("%s%s%s, %s", firstName, middleName, lastName, suffix);
    }

    public String subtitle()
    {
        return specialty;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        // dest.writeValue(_avatar);
        dest.writeValue(middleInitial);
        dest.writeValue(specialty);
        dest.writeValue(avatarUrl);
        dest.writeValue(clinic);
        dest.writeDouble(distance);
        dest.writeValue(firstName);
        dest.writeValue(physicianID);
        dest.writeValue(lastName);
        dest.writeValue(phoneNumber);
        dest.writeValue(suffix);
        ParcelHelper.writeBoolean(isSponsor, dest);
        ParcelHelper.writeBoolean(wasCalled, dest);
        dest.writeParcelable(officeLocation, flags);
        dest.writeList(insurancePlans);
    }

    @SuppressWarnings("unchecked")
    public Physician(final Parcel in)
    {
        // _avatar = (Bitmap) in.readValue(String.class.getClassLoader());
        middleInitial = (String)in.readValue(String.class.getClassLoader());
        specialty = (String)in.readValue(String.class.getClassLoader());
        avatarUrl = (String)in.readValue(String.class.getClassLoader());
        clinic = (String)in.readValue(String.class.getClassLoader());
        distance = in.readDouble();
        firstName = (String)in.readValue(String.class.getClassLoader());
        physicianID = (String)in.readValue(String.class.getClassLoader());
        lastName = (String)in.readValue(String.class.getClassLoader());
        phoneNumber = (String)in.readValue(String.class.getClassLoader());
        suffix = (String)in.readValue(String.class.getClassLoader());

        isSponsor = ParcelHelper.readBoolean(in);
        wasCalled = ParcelHelper.readBoolean(in);

        officeLocation =
                (OfficeLocation)in.readParcelable(OfficeLocation.class
                        .getClassLoader());

        insurancePlans = in.readArrayList(
                InsurancePlanAndCarrier.class.getClassLoader());
    }

    public static final Parcelable.Creator<Physician> CREATOR =
            new Parcelable.Creator<Physician>()
            {
                @Override
                public Physician createFromParcel(final Parcel in)
                {
                    return new Physician(in);
                }

                @Override
                public Physician[] newArray(final int size)
                {
                    return new Physician[size];
                }
            };
    // [endregion]

    // [region] helper methods

    // [endregion]

}
