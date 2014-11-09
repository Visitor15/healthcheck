package mobile.forged.com.health.controllers;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by visitor15 on 10/17/14.
 */
public interface ControllerCallback extends Serializable {

    public void handleCallback(Bundle b);
}
