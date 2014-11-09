package mobile.forged.com.health.vitals;

/**
 * Created by visitor15 on 11/9/14.
 */
public class Cholesterol {
    public Integer total;
    public Integer ldl;
    public Integer hdl;

    public Cholesterol(int total, int ldl, int hdl) {
        this.total = total;
        this.ldl = ldl;
        this.hdl = hdl;
    }

    public Cholesterol() {}

    @Override
    public Cholesterol clone()
    {
        Cholesterol clone = new Cholesterol();
        clone.total = this.total;
        clone.ldl = this.ldl;
        clone.hdl = this.hdl;
        return clone;
    }
}