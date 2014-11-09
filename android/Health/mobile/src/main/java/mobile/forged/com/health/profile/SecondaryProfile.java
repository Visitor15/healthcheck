package mobile.forged.com.health.profile;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by visitor15 on 11/9/14.
 */
public class SecondaryProfile extends BaseProfile {
    // [region] constants
    public static final String PROFILE_ID_KEY = "profileId";
    // [endregion]

    // [region] member variables
    public String identifier;

    // [endregion]

    // [region] cctors
    public SecondaryProfile(final String firstName, final String lastName,
                            final String email, final double heightInMeters,
                            final double weightInKg, final String gender, final Date dateOfBirth) {
        super(firstName, lastName, email, heightInMeters, weightInKg, gender,
                dateOfBirth);
    }

    public SecondaryProfile(final String name, final double heightInMeters,
                            final double weightInKg, final String gender, final Date dateOfBirth) {
        super(name, heightInMeters, weightInKg, gender, dateOfBirth);
    }

    public SecondaryProfile() {
        super(null, 0, 0, null, null);
    }

    public SecondaryProfile(String id)
    {
        super(null, 0, 0, null, null);
        this.identifier = id;
    }

    /*
     * clones BaseProfile but DOES NOT deep clone medications
     * @see java.lang.Object#clone()
     */
    @Override
    public SecondaryProfile clone()
    {
        SecondaryProfile clone = new SecondaryProfile(this.firstName, this.lastName,
                this.email, this.heightInMeters, this.weightInKg, this.gender,
                this.dateOfBirth == null ? null : (Date)this.dateOfBirth.clone());
        clone.avatarURI = this.avatarURI;
        clone.avatarURL = this.avatarURL;
        if (this.lastProfileFetchDate != null)
            clone.lastProfileFetchDate = (Date)this.lastProfileFetchDate.clone();
        if (this.insurancePlan != null)
            clone.insurancePlan = this.insurancePlan.clone();
        if (this.physician != null)
            clone.physician = this.physician.clone();
        if (this.avatar != null)
        {
            try
            {
                clone.avatar = Bitmap.createScaledBitmap(
                        this.avatar, this.avatar.getWidth(), this.avatar.getHeight(), false);
            }
            catch (Exception e)
            {
                clone.avatar = this.avatar;
            }
        }
        clone.isSelected = this.isSelected;

        clone.medications = this.medications;

        clone.identifier = this.identifier;
        return clone;
    }

    public String getName() {
        return firstName;
    }

    // [endregion]

    // [region] public methods
    public void setName(final String name) {
        firstName = name;
    }
    // [endregion]
}