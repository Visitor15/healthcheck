package mobile.forged.com.health.consultation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import mobile.forged.com.health.ParcelHelper;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Result implements Parcelable, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // [region] instance variables


    public final String title;
    public boolean emergency;
    public boolean flagged;
    public TextCauseDetail[] textCauseDetails;
    public String entNo;
    protected String recNo;
    protected int numMatched;
    protected int numTotal;
    protected String linkId;
    public ArrayList<ResultDetailSection> sections;
    public CauseType causeType;

    // [end region]


    // [region] constructors


    public Result(String title, boolean emergency,
                  String entNo, String recNo,
                  int numMatched, int numTotal, String linkId, CauseType causeType)
    {
        super();
        this.title = title;
        this.emergency = emergency;
        this.textCauseDetails = null;
        this.entNo = entNo;
        this.recNo = recNo;
        this.numMatched = numMatched;
        this.numTotal = numTotal;
        this.linkId = linkId;
        this.causeType = causeType;
    }


    public Result(String title, boolean flagged,
                  TextCauseDetail[] textCauseDetails )
    {
        this.title = title;
        this.emergency = flagged;
        this.textCauseDetails = textCauseDetails;
    }

    // [end region]

    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        ParcelHelper.writeBoolean(emergency, dest);
        ParcelHelper.writeBoolean(flagged, dest);
        if (textCauseDetails == null) {
            textCauseDetails = new TextCauseDetail[0];
        }
        dest.writeTypedList(Arrays.asList(textCauseDetails));
        dest.writeValue(entNo);
        dest.writeValue(recNo);
        dest.writeValue(numMatched);
        dest.writeValue(numTotal);
        dest.writeValue(linkId);
        dest.writeTypedList(sections);
        ParcelHelper.writeEnum(causeType, dest);
    }

    private Result(Parcel in) {
        title = (String) in.readValue(String.class.getClassLoader());
        emergency = ParcelHelper.readBoolean(in);
        flagged = ParcelHelper.readBoolean(in);
        ArrayList<TextCauseDetail> textCauseDetailsList = new ArrayList<TextCauseDetail>();
        in.readTypedList(textCauseDetailsList, TextCauseDetail.CREATOR);
        textCauseDetails = (TextCauseDetail[]) textCauseDetailsList.toArray(new TextCauseDetail[textCauseDetailsList.size()]);
        entNo = (String) in.readValue(String.class.getClassLoader());
        recNo = (String) in.readValue(String.class.getClassLoader());
        numMatched = (Integer) in.readValue(Integer.class.getClassLoader());
        numTotal = (Integer) in.readValue(Integer.class.getClassLoader());
        linkId = (String) in.readValue(String.class.getClassLoader());
        sections = new ArrayList<ResultDetailSection>();
        in.readTypedList(sections, ResultDetailSection.CREATOR);
        causeType = ParcelHelper.readEnum(CauseType.class, in);
    }

    public static final Parcelable.Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel source) {
            return new Result(source);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    // [endregion]

} // class Cause