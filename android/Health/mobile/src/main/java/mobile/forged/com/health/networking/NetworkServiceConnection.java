package mobile.forged.com.health.networking;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import mobile.forged.com.health.controllers.ControllerCallback;
import mobile.forged.com.health.services.NetworkService;
import mobile.forged.com.health.services.ServiceCallback;

/**
 * Created by nchampagne on 9/23/14.
 */
public class NetworkServiceConnection implements ServiceConnection {

    public static final int SERVICE_CONNECTED = 1;

    public static final int SERVICE_DISCONNECTED = 2;

    public static final int REQUEST_SUCCESS = 100;

    public static final int REQUEST_FAILURE = 200;

    public static final int REQUEST_ERROR = 300;

    public static final String RESPONSE_DATA = "response_data";

    private NetworkService mService;

    private boolean isBound;

    private Messenger mMessenger;

    private Messenger mCallbackMessenger = new Messenger(new ConnectionServiceHandler());

    private final ServiceCallback _callback;

    private List<Message> _messageQueue;

//    private GenericCallback mCallback;

//    private NetworkServiceConnection() {}

    public NetworkServiceConnection(ServiceCallback callback) {
        this._callback = callback;
        this._messageQueue = new ArrayList<Message>();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//        mServiceBinder = (BasicService.ServiceBinder) iBinder;
        mMessenger = new Messenger(iBinder);
        isBound = true;

        _callback.onServiceConnected(null);
        handleQueuedMessages();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound = false;
        mService = null;

        _callback.onServiceDisconnected(null);
    }

    public void sendRequestCommand(String url, Class type, NetworkService.RequestCommand command, ControllerCallback callback) {
        Bundle b = new Bundle();
        b.putSerializable("callback", callback);
        b.putString(NetworkService.REQUEST_TYPE, type.toString());
        b.putString(NetworkService.COMMAND_DATA, command.name());
        b.putString(NetworkService.REQUEST_URL, url);

        Message msg = obtainNewRequestMessage();
        msg.what = NetworkService.SEND_COMMAND_REQUEST;
        msg.setData(b);

        try {
            mMessenger.send(msg);
        } catch (Exception e) {
            addRequestToQueue(Message.obtain(msg));
        }
    }

    private void addRequestToQueue(Message msg) {
        _messageQueue.add(msg);
    }

    private void handleQueuedMessages() {
        for(Message m : _messageQueue) {
            try {
                mMessenger.send(m);
                _messageQueue.remove(m);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private Message obtainNewRequestMessage() {
        Message msg = new Message();
        msg.replyTo = mCallbackMessenger;
        return msg;
    }

    private void doControllerCallback(Bundle b) {
        if(b.containsKey("callback")) {
            ((ControllerCallback) b.getSerializable("callback")).handleCallback(b);
        }
    }

    private void parseData(Bundle b) {
        String s = b.getString(NetworkService.REQUEST_DATA);
    }

    public boolean isBound() {
        return isBound;
    }

    private void onHandleMessage(Message msg) {
        switch(msg.what) {
            case NetworkService.REQUEST_SUCCESS: {
                doControllerCallback(msg.getData());
                break;
            }
            case NetworkService.REQUEST_FAILURE: {
//                doErrorCallback(msg.getData());
                break;
            }
            case NetworkService.REQUEST_ERROR: {
                break;
            }
            default: {
                return;
            }
        }
    }

    /*
     * CLASS
     */
    protected class ConnectionServiceHandler extends Handler {

        protected ConnectionServiceHandler() {}

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                onHandleMessage(msg);
            }
        }
    }
}
