package mobile.forged.com.health.questionnaire;

import android.os.Parcel;
import android.os.Parcelable;

import com.nascentdigital.util.observing.Observable;
import com.nascentdigital.util.observing.ObservableField;

import java.io.Serializable;

import mobile.forged.com.health.ParcelHelper;
import mobile.forged.com.health.entities.AnswerValueType;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Answer extends Observable implements Parcelable, Serializable
{
    private static final long serialVersionUID = -909701388959207987L;
    // [region] constants

    public static final int FIELD_TEXT = 0;
    public static final int FIELD_ISCHECKED = 1;

    public static final String ENT_NO_TYPE_FEMALE = "12100";
    public static final String ENT_NO_TYPE_MALE = "3066";
    public static final String ENT_NO_TYPE_DOB = "31314";
    public static final String ENT_NO_TYPE_HEIGHT = "16645";
    public static final String ENT_NO_TYPE_WEIGHT = "16647";
    public static final String ENTITY_OTHER = "0";

    // [endregion]

    // [region] properties

    public String entNo;
    public String description;

    @ObservableField(FIELD_TEXT)
    public String text;

    @ObservableField(FIELD_ISCHECKED)
    public boolean isChecked;

    public String note;
    public AnswerValueType answerValType;
    public String unit;
    public String linkID;
    public String infoLink;
    public boolean hasInfo;
    public String minValue;
    public String maxValue;
    public String minmaxValueUnit;
    public String hideIfEntNoAbsent;
    public String hideIfEntNoPresent;
    public boolean isOther;
    public String keywordName;


    // [endregion]

    // [region] constructors

    public Answer(String entNo)
    {
        super();
        this.entNo = entNo;
    }

    public Answer(String entNo, String note)
    {
        super();
        this.entNo = entNo;
        this.note = note;
    }

    public Answer(String entNo, String description, String text,
                  AnswerValueType answerValType, String unit, String linkID, String minValue,
                  String maxValue, String minmaxValueUnit, String hideIfEntNoAbsent,
                  String hideIfEntNoPresent)
    {
        super();
        this.entNo = entNo;
        this.description = description;
        this.text = text;
        this.answerValType = answerValType;
        this.unit = unit;
        this.linkID = linkID;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minmaxValueUnit = minmaxValueUnit;
        this.hideIfEntNoAbsent = hideIfEntNoAbsent;
        this.hideIfEntNoPresent = hideIfEntNoPresent;
        this.hasInfo = this.linkID != null;
        this.isChecked = false;
    }

    // [endregion]

    // [region] parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(entNo);
        dest.writeValue(description);
        dest.writeValue(text);
        dest.writeValue(note);
        ParcelHelper.writeEnum(answerValType, dest);
        dest.writeValue(unit);
        dest.writeValue(linkID);
        dest.writeValue(infoLink);
        ParcelHelper.writeBoolean(hasInfo, dest);
        dest.writeValue(minValue);
        dest.writeValue(maxValue);
        dest.writeValue(minmaxValueUnit);
        dest.writeValue(hideIfEntNoAbsent);
        dest.writeValue(hideIfEntNoPresent);
        ParcelHelper.writeBoolean(isOther, dest);
        ParcelHelper.writeBoolean(isChecked, dest);
        dest.writeString(keywordName);
    }

    private Answer(Parcel in) {
        entNo = (String) in.readValue(String.class.getClassLoader());
        description = (String) in.readValue(String.class.getClassLoader());
        text = (String) in.readValue(String.class.getClassLoader());
        note = (String) in.readValue(String.class.getClassLoader());
        answerValType = ParcelHelper.readEnum(AnswerValueType.class, in);
        unit = (String) in.readValue(String.class.getClassLoader());
        linkID = (String) in.readValue(String.class.getClassLoader());
        infoLink = (String) in.readValue(String.class.getClassLoader());
        hasInfo = ParcelHelper.readBoolean(in);
        minValue = (String) in.readValue(String.class.getClassLoader());
        maxValue = (String) in.readValue(String.class.getClassLoader());
        minmaxValueUnit = (String) in.readValue(String.class.getClassLoader());
        hideIfEntNoAbsent = (String) in.readValue(String.class.getClassLoader());
        hideIfEntNoPresent = (String) in.readValue(String.class.getClassLoader());
        isOther = ParcelHelper.readBoolean(in);
        isChecked = ParcelHelper.readBoolean(in);
        keywordName = in.readString();
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    // [endregion]
}