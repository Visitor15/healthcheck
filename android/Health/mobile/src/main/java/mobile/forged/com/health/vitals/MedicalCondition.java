package mobile.forged.com.health.vitals;

/**
 * Created by visitor15 on 11/9/14.
 */
public class MedicalCondition
{
    public String identifier;
    public String fulltitle;
    public String title;
    public String subtitle;

    public MedicalCondition(String identifier, String name)
    {
        this.identifier = identifier;
        this.fulltitle = name;
        int start = name.indexOf("(");
        this.title = start > 0 ? name.substring(0, start) : name;
        this.subtitle = start > 0 ? name.substring(start) : null;
    }

    public MedicalCondition()
    {
    }
}