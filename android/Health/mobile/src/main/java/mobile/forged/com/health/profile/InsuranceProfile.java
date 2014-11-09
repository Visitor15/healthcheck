package mobile.forged.com.health.profile;

/**
 * Created by visitor15 on 11/9/14.
 */
public class InsuranceProfile {
    public InsurancePlanAndCarrier insurancePlanAndCarrier;
    public String identification;
    public String group;

    public InsuranceProfile(
            InsurancePlanAndCarrier insurancePlanAndCarrier,
            String identification,
            String group) {
        this.insurancePlanAndCarrier = insurancePlanAndCarrier;
        this.identification = identification;
        this.group = group;
    }

    public InsuranceProfile() {}

    public InsuranceProfile clone()
    {
        InsuranceProfile clone = new InsuranceProfile();
        if (this.insurancePlanAndCarrier != null)
            clone.insurancePlanAndCarrier = (InsurancePlanAndCarrier)this.insurancePlanAndCarrier.clone();
        clone.identification = this.identification;
        clone.group = this.group;
        return clone;
    }
}
