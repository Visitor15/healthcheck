package mobile.forged.com.health.controllers;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mobile.forged.com.health.common.Topic;
import mobile.forged.com.health.networking.NetworkServiceConnection;
import mobile.forged.com.health.services.NetworkService;

/**
 * Created by visitor15 on 9/30/14.
 */
public class RequestController extends BaseController implements ControllerCallback {

    private NetworkServiceConnection networkServiceConnection;

    private boolean isBound;

    public RequestController() {
        networkServiceConnection = new NetworkServiceConnection(this);
        initializeService(NetworkService.class, networkServiceConnection);
    }

//    @Override
//    public void onServiceConnected() {
//        // Automatically called from BaseController.
//    }
//
//    @Override
//    public void onServiceDisconnected() {
//        // Automatically called from BaseController.
//    }

    @Override
    public void onServiceRequestError() {
        // Automatically called from BaseController.
    }

    @Override
    public void onDeliverResults() {

    }

    private void createRequestWithUrlForAsyncResponse(String url, final Type type, final DataCallback callback) {

        networkServiceConnection.sendRequestCommand(url, type.getClass(), NetworkService.RequestCommand.ACCEPT_RETURN_JSON_RESPONSE, new ControllerCallback() {
            @Override
            public void handleCallback(Bundle b) {
                callback.receiveResults(b.getString(NetworkService.RESPONSE_DATA));
//                callback.receiveResults(convertJsonResponseToType(b.getString(NetworkService.RESPONSE_DATA), type));
            }
        });

//        networkServiceConnection.sendRequestForResponse(url, new ControllerCallback() {
//            @Override
//            public void handleCallback(Bundle b) {
//                callback.receiveResults(convertJsonToTopic(b.getString(NetworkService.RESPONSE_DATA), type));
//            }
//        });
    }

    public void getTopicsByTopicName(String url, final DataCallback callback) {
        createRequestWithUrlForAsyncResponse(url, new TypeToken<List<Topic>>() {
        }.getType(), callback);
    }

    public <T extends Type> List<T> convertJsonResponseToType(String jsonStr, T type) {
        Gson gson = new GsonBuilder().create();
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonStr);
            JSONArray childMetaData = obj.getJSONArray("child_data");
            JSONArray testArray = obj.getJSONArray("children");
//                List<MetaChildData> metaChildData = gson.fromJson(childMetaData.toString(), new TypeToken<List<MetaChildData>>() {
//                }.getType());eturn gson.fromJson(testArray.toString(), type);
        } catch (Exception e) {
            e.printStackTrace();
            // No child data present
            try {

                Object resultType = gson.fromJson(obj.toString(), type);
                List resultList = new ArrayList<T>();
                resultList.add(resultType);
                return resultList;
            } catch(Exception e2) {
                e2.printStackTrace();
            }
        }
        return new ArrayList<T>();
    }

//    public List<Topic> getAllTopics() {
//        List<Topic> topicList = new ArrayList<Topic>();
//
//        networkServiceConnection.sendRequest(KhanAcademy.TOPIC_TREE_URL);
//
//        return topicList;
//    }
//
//    public List<MathTopic> getAllMathTopics() {
//        List<MathTopic> topicList = new ArrayList<MathTopic>();
//
//        networkServiceConnection.sendRequest(KhanAcademy.TOPIC_TREE_URL);
//
//
//        return topicList;
//    }
//
//    public List<ScienceTopic> getAllScienceTopics() {
//        List<ScienceTopic> topicList = new ArrayList<ScienceTopic>();
//
//        networkServiceConnection.sendRequest(KhanAcademy.getTopicUrl(new ScienceTopic()));
//
//
//        return topicList;
//    }

//    @Override
//    public void onRequestSuccess(Bundle b) {
//        System.out.println("Got bundle: " + b);
//        if(b.containsKey("callback_data")) {
//            isBound = true;
////            List<ScienceTopic> mathTopics = getAllScienceTopics();
//        }
//
//        if(b.containsKey(NetworkService.REQUEST_DATA)) {
//            String response = b.getString(NetworkService.REQUEST_DATA);
//            System.out.println("GOT RESPONSE: " + response);
//        }
//    }

//    @Override
//    public void onRequestFailure(Bundle b) {
//
//    }

    @Override
    public void handleCallback(Bundle b) {

    }
}
