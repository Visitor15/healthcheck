package mobile.forged.com.health.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by visitor15 on 10/6/14.
 */
public abstract class GenericAsyncCallback implements Runnable {

    public static final String SERVICE_RESPONSE = "service_response";

    private Bundle b;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            notifyController(msg);
        }
    };

    private void notifyController(Message msg) {
        onHandleAsyncCallback(msg.getData());
    }

    public abstract void onHandleAsyncCallback(Bundle b);

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {

        System.out.println("I am in GenericAsyncCallback.");
    }


}
