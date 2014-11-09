package mobile.forged.com.health.vitals;

/**
 * Created by visitor15 on 11/9/14.
 */
public class BloodPressure {
    public Integer systolic;
    public Integer diastolic;

    public BloodPressure(int systolic, int diastolic) {
        this.systolic = systolic;
        this.diastolic = diastolic;
    }

    public BloodPressure() {}

    public BloodPressure clone()
    {
        BloodPressure clone = new BloodPressure();
        clone.systolic = systolic;
        clone.diastolic = diastolic;
        return clone;
    }
}