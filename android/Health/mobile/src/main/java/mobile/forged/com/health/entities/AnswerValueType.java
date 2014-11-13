package mobile.forged.com.health.entities;

import android.util.SparseArray;

import java.io.Serializable;

/**
 * Created by visitor15 on 11/12/14.
 */
public enum AnswerValueType implements Serializable
{
    DEFAULT ( 0),
    ABSOLUTE_NUMBER ( 1),
    STRING ( 2),
    DATE ( 3),
    TIME ( 4),
    TIMESTAMP ( 5),
    TIMESPAN ( 6),
    LENGTH ( 7),
    WEIGHT ( 8),
    CAPACITY ( 9),
    CURRENCY ( 10),
    TEMPERATURE ( 11),
    BLOOD_PRESSURE ( 12),
    CONCENTRATION ( 13),
    CONCENTRATION_UNIT ( 14),
    CLEARANCE_RATE ( 15),
    CELL_COUNT ( 16),
    MAX ( 17);

    private int intValue;

    private AnswerValueType(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    private static final SparseArray<AnswerValueType> intToTypeMap = new SparseArray<AnswerValueType>();
    static {
        for (AnswerValueType type : AnswerValueType.values()) {
            intToTypeMap.put(type.getIntValue(), type);
        }
    }

    public static AnswerValueType fromInt(int i) {
        AnswerValueType type = intToTypeMap.get(Integer.valueOf(i));
        if (type == null)
            return AnswerValueType.DEFAULT;
        return type;
    }
}