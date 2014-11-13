package mobile.forged.com.health.consultation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by visitor15 on 11/12/14.
 */
public class TextCauseDetail extends ResultDetailSection implements Serializable
{
    private static final long serialVersionUID = -8324638187178635614L;
    // [region] constants

    // [endregion]


    // [region] instance variables
    public final String description;

    // [endregion]


    // [region] constructors

    public TextCauseDetail(String categoryTitle, String description)
    {
        super(categoryTitle);
        this.description = description;
    }
    // [endregion]


    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(description);
    }

    private TextCauseDetail(Parcel in) {
        super(in);
        description = (String) in.readValue(String.class.getClassLoader());
    }

    public static final Parcelable.Creator<TextCauseDetail> CREATOR = new Parcelable.Creator<TextCauseDetail>() {
        @Override
        public TextCauseDetail createFromParcel(Parcel source) {
            return new TextCauseDetail(source);
        }

        @Override
        public TextCauseDetail[] newArray(int size) {
            return new TextCauseDetail[size];
        }
    };

    // [endregion]

}