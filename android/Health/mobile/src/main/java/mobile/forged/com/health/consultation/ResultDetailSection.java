package mobile.forged.com.health.consultation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.ArrayList;

import mobile.forged.com.health.ParcelHelper;

public class ResultDetailSection implements Parcelable, Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// [region] constants

	// [endregion]

	// [region] instance variables

	public final String title;
	public ResultDetailSectionType type;
	public boolean containsSubHeaders;
	public boolean isReferenceType;
	public ArrayList<Reference> refContents;
	public ArrayList<ResultDetail> details;
	public ArrayList<ResultDetailSection> causeDetailHeaderContents;

	// [endregion]

	// [region] constructors
	public ResultDetailSection(String title) {
		this.title = title;
	}

	// [endregion]

	// [region] parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(title);
		ParcelHelper.writeEnum(type, dest);
		ParcelHelper.writeBoolean(containsSubHeaders, dest);
		ParcelHelper.writeBoolean(isReferenceType, dest);
		dest.writeTypedList(refContents);
		dest.writeTypedList(details);
		dest.writeTypedList(causeDetailHeaderContents);
	}

	protected ResultDetailSection(Parcel in) {
		title = (String) in.readValue(String.class.getClassLoader());
		type = ParcelHelper.readEnum(
				ResultDetailSectionType.class, in);
		containsSubHeaders = ParcelHelper.readBoolean(in);
		isReferenceType = ParcelHelper.readBoolean(in);
		refContents = new ArrayList<Reference>();
		in.readTypedList(refContents, Reference.CREATOR);
		details = new ArrayList<ResultDetail>();
		in.readTypedList(details, ResultDetail.CREATOR);
		causeDetailHeaderContents = new ArrayList<ResultDetailSection>();
		in.readTypedList(causeDetailHeaderContents,
				ResultDetailSection.CREATOR);
	}

	public static final Creator<ResultDetailSection> CREATOR = new Creator<ResultDetailSection>() {
		@Override
		public ResultDetailSection createFromParcel(Parcel source) {
			return new ResultDetailSection(source);
		}

		@Override
		public ResultDetailSection[] newArray(int size) {
			return new ResultDetailSection[size];
		}
	};

	// [endregion]

}
