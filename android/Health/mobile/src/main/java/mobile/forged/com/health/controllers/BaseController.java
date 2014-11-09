package mobile.forged.com.health.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import mobile.forged.com.health.HealthApplication;
import mobile.forged.com.health.services.ServiceCallback;

/**
 * Created by visitor15 on 9/30/14.
 */
public abstract class BaseController implements ServiceCallback {

    private boolean isBound = false;

    protected boolean isServiceConnected = false;

    public BaseController() {

    }

    protected void initializeService(Class c, ServiceConnection serviceConnection) {
        isBound = false;
        HealthApplication.getReference().bindService(new Intent(HealthApplication.getReference(),
                        c),
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }



    public abstract void onServiceRequestError();

    public abstract void onDeliverResults();

    @Override
    public void onServiceConnected(Bundle b) {
        isServiceConnected = true;
    }

    @Override
    public void onServiceDisconnected(Bundle b) {
        isServiceConnected = false;
    }
}
