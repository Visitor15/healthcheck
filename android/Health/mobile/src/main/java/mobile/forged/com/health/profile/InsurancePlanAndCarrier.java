package mobile.forged.com.health.profile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by visitor15 on 11/9/14.
 */
public class InsurancePlanAndCarrier implements Parcelable
{
    public String planId;
    public String planName;
    public String carrierId;
    public String carrierName;

    public InsurancePlanAndCarrier(String planId, String planName,
                                   String carrierId, String carrierName)
    {
        this.planId = planId;
        this.planName = planName;
        this.carrierId = carrierId;
        this.carrierName = carrierName;
    }

    public InsurancePlanAndCarrier(InsuranceCarrier carrier, InsurancePlan plan)
    {
        this.planId = plan.identifier;
        this.planName = plan.name;
        this.carrierId = carrier.identifier;
        this.carrierName = carrier.name;
    }

    public InsurancePlanAndCarrier()
    {
    }

    @Override
    public InsurancePlanAndCarrier clone()
    {
        InsurancePlanAndCarrier clone = new InsurancePlanAndCarrier();
        clone.planId = this.planId;
        clone.planName = this.planName;
        clone.carrierId = this.carrierId;
        clone.carrierName = this.carrierName;
        return clone;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(planId);
        dest.writeString(planName);
        dest.writeString(carrierId);
        dest.writeString(carrierName);
    }

    public InsurancePlanAndCarrier(Parcel in)
    {
        planId = in.readString();
        planName = in.readString();
        carrierId = in.readString();
        carrierName = in.readString();
    }

    public static final Parcelable.Creator<InsurancePlanAndCarrier> CREATOR =
            new Parcelable.Creator<InsurancePlanAndCarrier>()
            {
                @Override
                public InsurancePlanAndCarrier createFromParcel(final Parcel in)
                {
                    return new InsurancePlanAndCarrier(in);
                }

                @Override
                public InsurancePlanAndCarrier[] newArray(final int size)
                {
                    return new InsurancePlanAndCarrier[size];
                }
            };

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && ((InsurancePlanAndCarrier)obj).planId.equals(planId)
                && ((InsurancePlanAndCarrier)obj).carrierId.equals(carrierId))
        {
            return true;
        }

        return false;
    }
}