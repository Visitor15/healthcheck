package mobile.forged.com.health;

import android.app.Application;

/**
 * Created by visitor15 on 11/9/14.
 */
public class HealthApplication extends Application {
    private static HealthApplication _singleton;

    @Override
    public void onCreate() {
        HealthApplication._singleton = this;
        super.onCreate();
    }

    public static HealthApplication getReference() {
        return HealthApplication._singleton;
    }
}
