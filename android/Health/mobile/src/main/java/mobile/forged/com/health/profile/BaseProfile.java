package mobile.forged.com.health.profile;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by visitor15 on 11/9/14.
 */
public class BaseProfile {

    public static final String FEMALE = "FEMALE";
    public static final String MALE = "MALE";

    public static final int FIELD_FIRST_NAME = 1;
    public static final int FIELD_LAST_NAME = 2;
    public static final int FIELD_EMAIL = 3;
    public static final int FIELD_HEIGHT = 4;
    public static final int FIELD_WEIGHT = 5;
    public static final int FIELD_GENDER = 6;
    public static final int FIELD_DATE_OF_BIRTH = 7;
    public static final int FIELD_PHYSICIAN = 8;
    public static final int FIELD_INSURANCE = 9;

    public String firstName;

    public String lastName;

    public String email;

    public Double heightInMeters;
    public Double weightInKg;
    public String gender;
    public Date dateOfBirth;

    public String avatarURI;
    public String avatarURL;
    public Date lastProfileFetchDate;
    public InsuranceProfile insurancePlan;
    public ProfilePhysician physician;
    public Bitmap avatar;
    public boolean isSelected;

    public ArrayList<Medication> medications;

    public void updateBaseInfo(final BaseProfile updatedProfile) {
        firstName = updatedProfile.firstName;
        lastName = updatedProfile.lastName;
        email = updatedProfile.email;
        heightInMeters = updatedProfile.heightInMeters;
        weightInKg = updatedProfile.weightInKg;
        gender = updatedProfile.gender;
        dateOfBirth = updatedProfile.dateOfBirth;
        lastProfileFetchDate = updatedProfile.lastProfileFetchDate;
    }

    public void update(final BaseProfile updatedProfile) {
        firstName = updatedProfile.firstName;
        lastName = updatedProfile.lastName;
        email = updatedProfile.email;
        heightInMeters = updatedProfile.heightInMeters;
        weightInKg = updatedProfile.weightInKg;
        gender = updatedProfile.gender;
        dateOfBirth = updatedProfile.dateOfBirth;
        lastProfileFetchDate = updatedProfile.lastProfileFetchDate;
        insurancePlan = updatedProfile.insurancePlan;
        physician = updatedProfile.physician;
        medications = updatedProfile.medications;
    }

    protected BaseProfile(final String firstName, final String lastName,
                          final String email, final double heightInMeters,
                          final double weightInKg, final String gender, final Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.heightInMeters = heightInMeters;
        this.weightInKg = weightInKg;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        medications = new ArrayList<Medication>();
    }

    protected BaseProfile(final String name, final double heightInMeters,
                          final double weightInKg, final String gender, final Date dateOfBirth) {
        firstName = name;
        this.heightInMeters = heightInMeters;
        this.weightInKg = weightInKg;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        medications = new ArrayList<Medication>();
    }

    protected BaseProfile() {
        medications = new ArrayList<Medication>();
    }

    @Override
    public String toString() {
        return "Profile [firstName=" + firstName + ", lastName=" + lastName
                + ", email=" + email + ", heightInMeters=" + heightInMeters
                + ", weightInKg=" + weightInKg + ", gender=" + gender
                + ", dateOfBirth=" + dateOfBirth + ", heightInFt="
                + heightInFt() + ", heightInIn=" + heightInIn()
                + ", weightInLbs=" + weightInLbs() + ", lastProfileFetchDate="
                + lastProfileFetchDate + ", physician=" + physician + "]";
    }

    public int heightInFt() {
        if (heightInMeters == null)
        {
            return 0;
        }
        final double heightInCm = heightInMeters * 100.0f;
        final double heightinIn = heightInCm / 2.54f;
        final int heightInFt = (int) (heightinIn / 12f);
        return heightInFt;
    }

    public void setHeightInInches(final int h) {
        heightInMeters = h * 0.0254;
    }

    public int heightInIn() {
        if (heightInMeters == null)
        {
            return 0;
        }
        final double heightInCm = heightInMeters * 100.f;
        final int heightinIn = (int) (heightInCm / 2.54f);
        final int heightInFt = (int) (heightinIn / 12.f);
        return (heightinIn - heightInFt * 12);
    }

    public int weightInLbs() {
        if (weightInKg == null) return 0;
        return (int) Math.round(weightInKg * 2.2);
    }

    public void setWeightInLbs(final int w) {
        weightInKg = w * 0.45359237;
    }

    public void addMedication(final Medication medication) {
        if (!matchFound(medication)) {
            medications.add(medication);
        }
    }

    public void addMedications(final Collection<Medication> medications) {
        final ArrayList<Medication> meds = new ArrayList<Medication>();
        for (final Medication m : medications) {
            if (!matchFound(m)) {
                meds.add(m);
            }
        }
        this.medications.addAll(medications);
    }

    private boolean matchFound(final Medication medication) {
        for (final Medication m : medications) {
            if (m.name.equalsIgnoreCase(medication.name)) {
                return true;
            }
        }
        return false;
    }
}