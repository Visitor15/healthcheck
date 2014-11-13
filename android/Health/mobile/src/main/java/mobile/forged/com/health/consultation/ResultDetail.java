package mobile.forged.com.health.consultation;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import mobile.forged.com.health.services.ContribFinding;


public class ResultDetail implements Parcelable, Serializable
{
	private static final long serialVersionUID = -6710739454252256277L;
	// [region] instance variables

	public String title;
	public ArrayList<ResultDetailDescription> descriptions;
	public String referenceNums;
	public String linkID;
	public ArrayList<ContribFinding> contribFindings;
	

	// [endregion]


	// [region] constructors
	
	public ResultDetail(String title, ArrayList<ResultDetailDescription> descriptions,
		String referenceNums, String linkID, ArrayList<ContribFinding> contribFindings)
	{
		super();
		this.title = title;
		this.descriptions = descriptions;
		this.referenceNums = referenceNums;
		this.linkID = linkID;
		this.contribFindings = contribFindings;
	}
	
	public ResultDetail(String title, ArrayList<ResultDetailDescription> descriptionArray){
		super();
		this.title = title;
		this.descriptions = descriptionArray;
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
		dest.writeTypedList(descriptions);
		dest.writeValue(referenceNums);
		dest.writeValue(linkID);
		dest.writeTypedList(contribFindings);
	}
	
	private ResultDetail(Parcel in) {
		title = (String) in.readValue(String.class.getClassLoader());
		descriptions = new ArrayList<ResultDetailDescription>();
		in.readTypedList(descriptions, ResultDetailDescription.CREATOR);
		referenceNums = (String) in.readValue(String.class.getClassLoader());
		linkID = (String) in.readValue(String.class.getClassLoader());
		contribFindings = new ArrayList<ContribFinding>();
		in.readTypedList(contribFindings, ContribFinding.CREATOR);
	}
	
	public static final Creator<ResultDetail> CREATOR = new Creator<ResultDetail>() {
		@Override
		public ResultDetail createFromParcel(Parcel source) {
			return new ResultDetail(source);
		}

		@Override
		public ResultDetail[] newArray(int size) {
			return new ResultDetail[size];
		}
	};

	// [endregion]
}
