package mobile.forged.com.health.utilities;

/**
 * Created by visitor15 on 11/9/14.
 */
public class StringHelper {
    public static boolean isNullOrEmpty (String str)
    {
        return (str == null || str.isEmpty() );
    }

    public static boolean isNullOrWhitespace (String str)
    {
        return (str == null || str.trim().isEmpty() );
    }
}