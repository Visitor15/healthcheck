package mobile.forged.com.health.profile;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mobile.forged.com.health.consultation.ConsultSummary;

/**
 * Created by visitor15 on 11/9/14.
 */
public class Profile extends BaseProfile
{
    public static final int FIELD_VITAL_STATS = 0;
    public static final int FIELD_SECONDARY_PROFILE = 1;


    public ArrayList<ConsultSummary> consultSummaries;
    public ArrayList<SecondaryProfile> secondaryProfiles;

    public VitalStats vitalStats = new VitalStats(true);

    public Profile(String firstName, String lastName, String email,
                   double heightInMeters, double weightInKg, String gender,
                   Date dateOfBirth)
    {
        super(firstName, lastName, email, heightInMeters, weightInKg, gender,
                dateOfBirth);
        secondaryProfiles = new ArrayList<SecondaryProfile>();
    }

    public Profile()
    {
        super();
        secondaryProfiles = new ArrayList<SecondaryProfile>();
    }

    /*
     * clones profile
     * but does NOT deep clone medications, consultSummaries and secondaryProfiles
     * @see com.sharecare.askmd.models.BaseProfile#clone()
     */
    @Override
    public Profile clone()
    {
        Profile clone = new Profile(this.firstName, this.lastName,
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
        clone.consultSummaries = this.consultSummaries;
        clone.secondaryProfiles = this.secondaryProfiles;
        if (this.vitalStats != null)
            clone.vitalStats = this.vitalStats.clone();
        return clone;
    }

    public void updateMedicalInfo(Profile profile)
    {
        if (vitalStats == null)
            vitalStats = profile.vitalStats;
        else
            vitalStats.updateVitalStats(profile.vitalStats);
    }

    public SecondaryProfile secondaryProfileWithId(String profileId)
    {
        for (SecondaryProfile sp : secondaryProfiles)
        {
            if (sp.identifier.equals(profileId))
            {
                return sp;
            }
        }
        return null;
    }


    public SecondaryProfile secondaryProfileWithName(String profileName)
    {
        for (SecondaryProfile sp : secondaryProfiles)
        {
            if (sp.getName().toLowerCase(Locale.US).equals(profileName.toLowerCase(Locale.US)))
            {
                return sp;
            }
        }
        return null;
    }
}