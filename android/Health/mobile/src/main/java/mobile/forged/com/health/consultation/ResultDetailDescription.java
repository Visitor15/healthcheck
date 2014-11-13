package mobile.forged.com.health.consultation;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class ResultDetailDescription implements Parcelable, Serializable
{
	private static final long serialVersionUID = 7179424583402383108L;
	
	public String title;
	public String referenceNums;

	public ResultDetailDescription(String title, String referenceNums)
	{
		super();
		this.title = title;
		this.referenceNums = referenceNums;
	}

	// [region] parcelable

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(title);
		dest.writeValue(referenceNums);
	}

	private ResultDetailDescription(Parcel in)
	{
		title = (String)in.readValue(String.class.getClassLoader());
		referenceNums = (String)in.readValue(String.class.getClassLoader());
	}

	public static final Creator<ResultDetailDescription> CREATOR =
		new Creator<ResultDetailDescription>()
		{
			@Override
			public ResultDetailDescription createFromParcel(Parcel source)
			{
				return new ResultDetailDescription(source);
			}

			@Override
			public ResultDetailDescription[] newArray(int size)
			{
				return new ResultDetailDescription[size];
			}
		};

	// [endregion]
}
