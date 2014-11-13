package mobile.forged.com.health.consultation;

import android.os.Parcel;
import android.os.Parcelable;

import com.nascentdigital.util.observing.ObservableArrayList;

import java.io.Serializable;

import mobile.forged.com.health.ParcelHelper;

/**
 * Created by visitor15 on 11/12/14.
 */
public class ResultCategory implements Parcelable, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 500L;
    // [region] constants

    public static final String COMMON_CAUSES = "Common Causes";
    public static final String LESS_COMMON_CAUSES = "Less Common Causes";
    public static final String POSSIBLE_MENTAL_HEALTH_PROBLEMS = "Possible Mental Health Problems";

    // [endregion]

    // [region] properties

    public final String name;
    public ObservableArrayList<Result> results;
    public boolean isOverviewInMgmt;
    public boolean isExpanded;

    // [endregion]

    // [region] constructors

    public ResultCategory(final String name)
    {
        this.name = name;
        this.isExpanded = false;
        this.results = new ObservableArrayList<Result>();
    }

    public ResultCategory(final String mName,
                          final ObservableArrayList<Result> observableCauses)
    {
        super();
        this.name = mName;
        this.results = observableCauses;
    }

    // [endregion]

    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeValue(name);
        dest.writeTypedList(results);
        ParcelHelper.writeBoolean(isOverviewInMgmt, dest);
    }

    private ResultCategory(final Parcel in) {
        name = (String) in.readValue(String.class.getClassLoader());
        results = new ObservableArrayList<Result>();
        in.readTypedList(results, Result.CREATOR);
        isOverviewInMgmt = ParcelHelper.readBoolean(in);
    }

    public static final Parcelable.Creator<ResultCategory> CREATOR = new Parcelable.Creator<ResultCategory>() {
        @Override
        public ResultCategory createFromParcel(final Parcel source) {
            return new ResultCategory(source);
        }

        @Override
        public ResultCategory[] newArray(final int size) {
            return new ResultCategory[size];
        }
    };

    // [endregion]
}