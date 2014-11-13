package mobile.forged.com.health.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import mobile.forged.com.health.ParcelHelper;
import mobile.forged.com.health.services.Coupler;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Topic implements Parcelable, Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 200L;
    // [region] constants
    public static final String CATEGORY_NO_GUIDANCE = "Generic";
    public static final String CATEGORY_DIAGNOSTIC = "Dx";
    public static final String CATEGORY_MANAGEMENT =  "Mgt";
    public static final String CATEGORY_INFOCARD = "InfoCard";

    // [end region]

    // [region] instance variables


    public Coupler coupleInfo;

    public String topicName;
    public String topicId;
    public String infocardID = "";
    public String topicRev;
    public TopicType type;
    public String topicCategory;


    // [endregion]


    // [region] constructors
    public Topic()
    {
    }

    public Topic(Coupler coupleInfo, TopicType type, String category)
    {
        this.coupleInfo = coupleInfo;
        this.type = type;
        this.topicCategory = category;
    }


    @Override
    public String toString()
    {
        return coupleInfo.toString();
    }

    // [endregion]

    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(coupleInfo, flags);
        dest.writeString(topicName);
        dest.writeString(topicId);
        dest.writeString(infocardID);
        dest.writeString(topicRev);
        ParcelHelper.writeEnum(type, dest);
        dest.writeString(topicCategory);
    }

    private Topic(Parcel in) {
        coupleInfo = in.readParcelable(Coupler.class.getClassLoader());
        topicName = in.readString();
        topicId = in.readString();
        infocardID = in.readString();
        topicRev = in.readString();
        type = ParcelHelper.readEnum(TopicType.class, in);
        topicCategory = in.readString();
    }

    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel source) {
            return new Topic(source);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
    // [endregion]


}
