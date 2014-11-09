package mobile.forged.com.health.controllers;

/**
 * Created by visitor15 on 9/30/14.
 */
public class KhanAcademyController extends BaseController {
//    @Override
//    public void onServiceConnected() {
//
//    }
//
//    @Override
//    public void onServiceDisconnected() {
//
//    }

    @Override
    public void onServiceRequestError() {

    }

    @Override
    public void onDeliverResults() {

    }

//    @Override
//    public void handleCallback(Bundle b) {
//
//    }
//
//    private final NetworkServiceConnection networkServiceConnection;
//
//    public KhanAcademyController() {
//        networkServiceConnection = new NetworkServiceConnection(KhanAcademyController.this);
//        initializeService(NetworkService.class, networkServiceConnection);
//    }
//
//    @Override
//    public void onServiceConnected() {
//        // Automatically called from BaseController.
//    }
//
//    @Override
//    public void onServiceDisconnected() {
//        // Automatically called from BaseController.
//    }
//
//    @Override
//    public void onServiceRequestError() {
//        // Automatically called from BaseController.
//    }
//
//    @Override
//    public void onDeliverResults() {
//        // Automatically called from BaseController.
//    }
//
//    private void sendRequestForAsyncResponse(String id, final Type type, final DataCallback callback) {
//        networkServiceConnection.sendRequestForResponse(KhanAcademy.getTopicUrl(id), new ControllerCallback() {
//            @Override
//            public void handleCallback(Bundle b) {
//                callback.receiveResults(convertJsonToTopic(b.getString(NetworkService.RESPONSE_DATA), type));
//            }
//        });
//    }
//
//    private void createRequestWithUrlForAsyncResponse(String url, final Type type, final DataCallback callback) {
//
//        networkServiceConnection.sendRequestCommand(url, type.getClass(), NetworkService.RequestCommand.ACCEPT_PARSE_JSON_RESPONSE, new ControllerCallback() {
//            @Override
//            public void handleCallback(Bundle b) {
//                callback.receiveResults(convertJsonToTopic(b.getString(NetworkService.RESPONSE_DATA), type));
//            }
//        });
//
////        networkServiceConnection.sendRequestForResponse(url, new ControllerCallback() {
////            @Override
////            public void handleCallback(Bundle b) {
////                callback.receiveResults(convertJsonToTopic(b.getString(NetworkService.RESPONSE_DATA), type));
////            }
////        });
//    }
//
//    private void createTopicTreeUrlRequestAsync(final DataCallback callback) {
////        networkServiceConnection.sendRequestForResponse(KhanAcademy.TOPIC_TREE_URL, new ControllerCallback() {
////            @Override
////            public void handleCallback(Bundle b) {
////                callback.receiveResults(parseTopicTree(b.getString(NetworkService.RESPONSE_DATA)));
////            }
////        });
//
//        networkServiceConnection.sendRequestCommand(KhanAcademy.TOPIC_TREE_URL, GenericTopic.class, NetworkService.RequestCommand.ACCEPT_PARSE_JSON_RESPONSE, new ControllerCallback() {
//            @Override
//            public void handleCallback(Bundle b) {
//                callback.receiveResults(parseTopicTree(b.getString(NetworkService.RESPONSE_DATA)));
//            }
//        });
//    }
//
//    public void getAllMathTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(MathTopic.MATH_ID, new TypeToken<List<MathTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getPlaylists(final DataCallback callback) {
//
//        sendRequestForAsyncResponse(MathTopic.MATH_ID, new TypeToken<List<MathTopic>>() {
//        }.getType(), callback);
//
//        networkServiceConnection.sendRequestForResponse(KhanAcademy.getTopicUrl(MathTopic.MATH_ID), new ControllerCallback() {
//            @Override
//            public void handleCallback(Bundle b) {
//                callback.receiveResults(convertJsonToTopic(b.getString(NetworkService.RESPONSE_DATA), new TypeToken<List<MathTopic>>() {
//                }.getType()));
//            }
//        });
//    }
//
//    public void getAllScienceTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(ScienceTopic.SCIENCE_ID, new TypeToken<List<ScienceTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAllEconomicsAndFinanceTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(EconomicsAndFinanceTopic.ECONOMICS_AND_FINANCE_ID, new TypeToken<List<EconomicsAndFinanceTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAllArtsAndHumanitiesTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(ArtsAndHumanitiesTopic.ARTS_AND_HUMANITIES_ID, new TypeToken<List<ArtsAndHumanitiesTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAllComputingTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(ComputingTopic.COMPUTING_TOPIC_ID, new TypeToken<List<ComputingTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAllTestPrepTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(TestPrepTopic.TEST_PREP_TOPIC_ID, new TypeToken<List<TestPrepTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAllPartnerContentTopics(final DataCallback callback) {
//        sendRequestForAsyncResponse(PartnerContentTopic.PARTNER_CONTENT_ID, new TypeToken<List<PartnerContentTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAllTalksAndInterviews(final DataCallback callback) {
//        sendRequestForAsyncResponse(TalksAndInterviewsTopic.TALKS_AND_INTERVIEWS_TOPIC_ID, new TypeToken<List<TalksAndInterviewsTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getAll(final DataCallback callback) {
//
//        createTopicTreeUrlRequestAsync(callback);
//
////        createRequestWithUrlForAsyncResponse(KhanAcademy.TOPIC_TREE_URL, new TypeToken<List<Topic>>() {
////        }.getType(), callback);
//    }
//
//    public void getTopicsByTopicName(String topicName, final DataCallback callback) {
//        createRequestWithUrlForAsyncResponse(KhanAcademy.getTopicUrl(convertToFriendlyString(topicName)), new TypeToken<List<GenericTopic>>() {
//        }.getType(), callback);
//    }
//
//    public void getVideoTopicsForTopic(String topicName, final DataCallback<VideoTopic> callback) {
//        createRequestWithUrlForAsyncResponse(KhanAcademy.getVideoTopicUrl(convertToFriendlyString(topicName)), new TypeToken<VideoTopic>() {
//        }.getType(), callback);
//    }
//
//    public List<Topic> parseTopicTree(String jsonTopicTree) {
//        Gson gson = new GsonBuilder().create();
//        JSONObject obj = null;
//
//        try {
//            obj = new JSONObject(jsonTopicTree);
//            return gson.fromJson(jsonTopicTree, new TypeToken<List<GenericTopic>>() {
//            }.getType());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return new ArrayList<Topic>();
//    }
//
//    public List<Topic> convertJsonToTopic(String jsonStr, Type type) {
//        Gson gson = new GsonBuilder().create();
//        JSONObject obj = null;
//        try {
//            obj = new JSONObject(jsonStr);
//            JSONArray childMetaData = obj.getJSONArray("child_data");
//            JSONArray testArray = obj.getJSONArray("children");
////                List<MetaChildData> metaChildData = gson.fromJson(childMetaData.toString(), new TypeToken<List<MetaChildData>>() {
////                }.getType());eturn gson.fromJson(testArray.toString(), type);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // No child data present
//            try {
//
//                Object resultType = gson.fromJson(obj.toString(), type);
//                List resultList = new ArrayList<Object>();
//                resultList.add(resultType);
//                return resultList;
//            } catch(Exception e2) {
//                e2.printStackTrace();
//            }
//        }
//        return new ArrayList<Topic>();
//    }
//
//    @Override
//    public void onRequestSuccess(Bundle b) {
//
//        Gson gson = new GsonBuilder().create();
//
//
//
//
//        ((ControllerCallback) b.getSerializable("callback")).handleCallback(b);
//    }
//
//    @Override
//    public void onRequestFailure(Bundle b) {
//        Toast.makeText(Expedition.getReference(), "Error requesting service: " + b.getString(NetworkServiceConnection.RESPONSE_DATA, ""), Toast.LENGTH_LONG).show();
//    }
//
//    private String convertToFriendlyString(String str) {
//        return str.replace(" ", "_").toLowerCase(Locale.getDefault());
//    }
//
//    @Override
//    public void handleCallback(Bundle b) {
//
//    }
}
