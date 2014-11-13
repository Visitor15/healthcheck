package mobile.forged.com.health.consultation;

import java.util.List;

/**
 * Created by visitor15 on 11/12/14.
 */
public class Specialty {
    public String identifier;
    public String name;
    public List<SpecialtyTag> tags;

    public Specialty(String identifier, String name, List<SpecialtyTag> tags) {
        this.identifier = identifier;
        this.name = name;
        this.tags = tags;
    }
}