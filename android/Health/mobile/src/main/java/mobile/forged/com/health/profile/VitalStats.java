package mobile.forged.com.health.profile;

import java.util.ArrayList;

import mobile.forged.com.health.vitals.BloodPressure;
import mobile.forged.com.health.vitals.Cholesterol;
import mobile.forged.com.health.vitals.MedicalCondition;

/**
 * Created by visitor15 on 11/9/14.
 */
public class VitalStats
{
    public enum TimeSinceLastVisitType
    {
        ThisMonth,
        WithinOneYear,
        OneTwoYears,
        ThreeFiveYears,
        FivePlusYears,
        DontGo
    }

    public boolean isPlaceHolder;
    public Cholesterol cholesterol;
    public Integer triglycerides;
    public BloodPressure bloodPressure;
    public ArrayList<MedicalCondition> medicalConditions;
    private TimeSinceLastVisitType mLastVisitType;

    public VitalStats(Cholesterol cholesterol, int triglycerides,
                      BloodPressure bloodPressure, TimeSinceLastVisitType lastVisitType)
    {
        this.cholesterol = cholesterol;
        this.triglycerides = triglycerides;
        this.bloodPressure = bloodPressure;
        this.mLastVisitType = lastVisitType;
        this.medicalConditions = new ArrayList<MedicalCondition>();
    }

    public VitalStats()
    {
        this.medicalConditions = new ArrayList<MedicalCondition>();
    }

    public VitalStats(boolean placeholder)
    {
        this.isPlaceHolder = placeholder;
        this.medicalConditions = new ArrayList<MedicalCondition>();
        this.cholesterol = new Cholesterol();
        this.bloodPressure = new BloodPressure();
    }

    /*
     * clones vitalstats but does not deep clone conditions
     * @see java.lang.Object#clone()
     */
    @Override
    public VitalStats clone()
    {
        VitalStats clone = new VitalStats();
        clone.isPlaceHolder = this.isPlaceHolder;
        if (this.cholesterol != null)
            clone.cholesterol = (Cholesterol)this.cholesterol.clone();
        clone.triglycerides = this.triglycerides;
        if (this.bloodPressure != null)
            clone.bloodPressure = (BloodPressure)this.bloodPressure.clone();
        clone.medicalConditions = this.medicalConditions;
        clone.mLastVisitType = this.mLastVisitType;
        return clone;
    }

    public void updateVitalStats(VitalStats vitalStats)
    {
        this.cholesterol = vitalStats.cholesterol;
        this.triglycerides = vitalStats.triglycerides;
        this.bloodPressure = vitalStats.bloodPressure;
        if (this.medicalConditions != vitalStats.medicalConditions)
        {
            this.medicalConditions.clear();
            for (MedicalCondition condition : vitalStats.medicalConditions)
            {
                medicalConditions.add(condition);
            }
        }
    }

    public TimeSinceLastVisitType getLastVisitType()
    {
        return mLastVisitType;
    }

    public void addCondition(MedicalCondition condition)
    {
        int index = getIndexToInsertCondition(condition);
        if (index < 0)
        {
            return; // Condition already added.
        }

        if (index == medicalConditions.size())
        {
            medicalConditions.add(condition);
        }
        else
        {
            medicalConditions.add(index, condition);
        }
    }

    public void removeCondition(MedicalCondition condition)
    {
        medicalConditions.remove(condition);
    }

    private int getIndexToInsertCondition(MedicalCondition condition)
    {
        int index = 0;
        for (MedicalCondition mc : medicalConditions)
        {
            int compareResult =
                    mc.fulltitle.compareToIgnoreCase(condition.fulltitle);
            if (compareResult == 0)
            {
                return -1; // No need to add because condition is already added.
            }
            else if (compareResult > 0)
            {
                break;
            }
            index++;
        }
        return index;
    }
}