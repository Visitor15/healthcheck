package mobile.forged.com.health.services;

import android.os.Bundle;

/**
 * Created by visitor15 on 11/9/14.
 */
public interface ServiceCallback {
    public void onServiceConnected(Bundle b);

    public void onServiceDisconnected(Bundle b);
}
