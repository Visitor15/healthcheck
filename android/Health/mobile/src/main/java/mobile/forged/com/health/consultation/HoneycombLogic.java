package mobile.forged.com.health.consultation;

import java.util.Map;

/**
 * Created by visitor15 on 11/12/14.
 */
public class HoneycombLogic {
    public enum HoneycombLogicCellType {
        Empty, CurrentEmpty, CurrentFilled, SubmittedEmpty, SubmittedFilled, Cause
    }

    public Map<String, String> markedIndices;
}