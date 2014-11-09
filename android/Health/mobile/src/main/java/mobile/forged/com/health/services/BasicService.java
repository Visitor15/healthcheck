package mobile.forged.com.health.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;

/**
 * Created by nchampagne on 9/23/14.
 */
public abstract class BasicService extends Service {

    public static final int MSG_REGISTER_CLIENT = 100;

    public static final int MSG_UNREGISTER_CLIENT = 200;

    public static final int MSG_REGISTRATION_SUCCESSFUL = 1;

    public static final String MESSENGER_ID = "messenger_id";

    public static final int SERVICE_CONNECTED = 1;

    public static final int SERVICE_DISCONNECTED = 2;

    public static final int SERVICE_REQUEST_ERROR = 3;

    public static final int DELIVER_RESULTS = 4;

    Looper mServiceLooper;

    Messenger mMessenger;

    ServiceHandler mServiceHandler;

    public BasicService() {}

    protected abstract void onHandleServiceMessage(final Message msg);

    protected abstract void onHandleWorkerMessage(final Message msg);

    protected void killCurrentWorkerThread() {
        if(Thread.currentThread() instanceof HandlerThread) {
            ((HandlerThread) Thread.currentThread()).quit();
        }
    }

    @Override
    public abstract void onDestroy();

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mMessenger = new Messenger(mServiceHandler);
    }

    @Override
    public abstract int onStartCommand(Intent intent, int flags, int startId);

    @Override
    public abstract boolean onUnbind(Intent intent);

    @Override
    public abstract void onRebind(Intent intent);

    protected void doCallback(Message msg) {
        try {
            msg.replyTo.send(Message.obtain(msg));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     * CLASS
     */
    public class ServiceBinder extends Binder {
        /*
         * Returns an instance of the current service.
         */
        public BasicService getService() {
            return BasicService.this;
        }
    }

    /*
     * CLASS
     */
    protected class ServiceHandler extends Handler {

        protected ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {



                onHandleServiceMessage(msg);
            }
        }
    }

    /*
     * CLASS
     */
    protected class ServiceWorkerHandler extends Handler {

        protected ServiceWorkerHandler(Looper looper) { super(looper); }

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                onHandleWorkerMessage(msg);
            }
        }
    }
}
