package mobile.forged.com.health.networking;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import mobile.forged.com.health.services.SharecareToken;

/**
 * Created by visitor15 on 9/22/14.
 */
public class HttpConnector {


    private enum AssetSize
    {
        Size300x300,
        Size240x240,
        Size120x120,
        Size290x290,
        Size298x248,
        Size60x60
    }

    private static final int TIME_LIMIT_IN_MINUTES = 60;

    private static final String SERVICE_CLIENT_VERSION = "1.06";
    private static final String OLDEST_SUPPORTED_VERSION = "1.06";

    private static final String DATA_SERVICE_URL;
    private static final String AUTH_SERVICE_URL;
    private static final String CLIENT_ID;
    private static final String CLIENT_SECRET;

    // Authorization endpoints
    private static final String AUTHORIZATION_HEADER;
    private static final String REGISTER_ENDPOINT;
    private static final String LOGIN_ENDPOINT;
    private static final String REFRESH_TOKEN_ENDPOINT;

    // Password endpoints
    private static final String FORGOT_PASSWORD_ENDPOINT;
    private static final String CHANGE_PASSWORD_ENDPOINT;

    // Profile endpoints
    private static final String ASKMD_PROFILE_ENDPOINT;
    private static final String PROFILE_URI_ENDPOINT;
    private static final String PROFILE_ENDPOINT;
    private static final String UPDATE_PROFILE_ENDPOINT;
    private static final String MEDICAL_INFO_ENDPOINT;

    // Secondary profile endpoints
    private static final String ADD_FAMILY_ENDPOINT;
    private static final String EDIT_FAMILY_ENDPOINT;
    private static final String DELETE_FAMILY_ENDPOINT;

    // Consult endpoints
    private static final String ALL_CONSULTS_ENDPOINT;
    private static final String DELETE_CONSULT_ENDPOINT;
    private static final String GET_CONSULTS_ENDPOINT;
    private static final String GET_SESSION_DOCUMENT_ENDPOINT;
    private static final String EDIT_CONSULT_ENDPOINT;
    private static final String ADD_DOCUMENT_ENDPOINT;

    // Share endpoints
    private static final String EMAIL_EXISTS_ENDPOINT;
    private static final String EMAIL_CONSULT_ENDPOINT;
    private static final String PRINT_CONSULT_ENDPOINT;

    // Consult asset endpoints
    private static final String GET_ALL_CONSULT_ASSETS_ENDPOINT;
    private static final String ADD_CONSULT_ASSET_ENDPOINT;
    private static final String DELETE_CONSULT_ASSET_ENDPOINT;

    // Medication endpoints
    private static final String GET_MEDICATIONS_ENDPOINT;
    private static final String ADD_MEDICATION_ENDPOINT;
    private static final String DELETE_MEDICATION_ENDPOINT;

    // Secondary medication endpoints
    private static final String GET_SECONDARY_MEDICATIONS_ENDPOINT;
    private static final String ADD_SECONDARY_MEDICATION_ENDPOINT;
    private static final String DELETE_SECONDARY_MEDICATION_ENDPOINT;

    // Condition endpoints
    private static final String GET_CONDITIONS_ENDPOINT;

    // Physician endpoints
    private static final String GET_SPECIALTIES_ENDPOINT;
    private static final String GET_INSURANCE_CARRIERS_ENDPOINT;
    private static final String GET_INSURANCE_PLANS_ENDPOINT;
    private static final String FIND_PHYSICIANS_ENDPOINT;
    private static final String FIND_SPONSORED_PHYSICIANS_ENDPOINT;
    private static final String GET_HOSPITAL_BY_LOCATION_NAME_ENDPOINT;
    private static final String GET_HOSPITAL_BY_LOCATION_ENDPOINT;

    // Json keys
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String REMEMBER_ME = "rememberMe";
    private static final String HAS_ACCEPTED_TERMS_AND_CONDITIONS =
            "hasAcceptedTermsAndConditions";

    public static final String PROFILE = "profile";
    public static final String ID = "id";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String GENDER = "gender";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String HEIGHT = "height";
    private static final String WEIGHT = "weight";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String TYPE = "type";
    public static final String URL = "url";
    public static final String AVATAR = "avatar";
    private static final String DESCRIPTION = "description";
    private static final String PROFILE_AVATAR = "ProfileAvatar";
    private static final String LOCATIONS = "locations";

    private static final String PHYSICIAN = "physician";
    private static final String NAME = "name";
    private static final String SPECIALTY = "specialty";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ZIP = "zip";
    private static final String PHONE = "phone";

    private static final String INSURANCE = "insurance";
    private static final String MEMBER_ID = "memberId";
    private static final String GROUP_ID = "groupId";
    private static final String PLAN = "plan";
    private static final String PLAN_ID = "planId";
    private static final String PLAN_NAME = "planName";
    private static final String CARRIER_ID = "carrierId";
    private static final String CARRIER_NAME = "carrierName";

    private static final String VITAL_STATS = "vitalStatistics";
    private static final String CHOLESTEROL = "cholesterol";
    private static final String TOTAL = "total";
    private static final String LDL = "ldl";
    private static final String HDL = "hdl";
    private static final String BLOOD_PRESSURE = "bloodPressure";
    private static final String SYSTOLIC = "systolic";
    private static final String DIASTOLIC = "diastolic";
    public static final String MEDICAL_CONDITIONS = "medicalConditions";
    private static final String TRIGLYCERIDES = "triglycerides";
    private static final String TIME_SINCE_LAST_VISIT = "timeSinceLastVisit";
    private static final String THIS_MONTH = "THIS_MONTH";
    private static final String WITHIN_ONE_YEAR = "WITHIN_ONE_YEAR";
    private static final String ONE_TWO_YEARS = "ONE_TWO_YEARS";
    private static final String THREE_FIVE_YEARS = "THREE_FIVE_YEARS";
    private static final String FIVE_PLUS_YEARS = "FIVE_PLUS_YEARS";
    private static final String DONT_GO = "DONT_GO";

    private static final String TITLE = "title";
    private static final String METADATA = "metadata";
    private static final String VERSION = "version";
    public static final String DOCUMENT = "document";
    private static final String DELETED = "deleted";
    private static final String SESSION = "session";
    private static final String SESSION_DOC_ID = "sessionDocID";
    private static final String FND_LIST = "fndList";
    private static final String FND_LIST_DOC_ID = "fndListDocID";
    private static final String USER_DATA = "userData";
    private static final String USER_DATA_PRIVATE = "userDataPrivate";
    private static final String USER_DATA_PRIVATE_DOC_ID =
            "userDataPrivateDocID";
    private static final String POPT_LIST = "poptList";
    private static final String POPT_LIST_DOC_ID = "poptListDocID";
    private static final String DATE_ADDED = "dateAdded";
    private static final String CATEGORY = "category";
    private static final String TOPIC_ID = "topicID";
    private static final String INFOCARD_ID = "infocardID";
    private static final String REV = "rev";
    private static final String PROFILE_ID = "profileID";
    private static final String NUM_CAUSES_FLAGGED = "numCausesFlagged";
    private static final String NUM_PHYSICIANS_CALLED = "numPhysiciansCalled";
    private static final String HAS_NOTE = "hasNote";
    private static final String PHYSICIANS_CALLED = "physiciansCalled";
    private static final String CHECKED_MEDS = "checkedMeds";
    private static final String NOTES = "notes";
    private static final String MARKED_INDICES = "markedIndices";
    private static final String RESULT_FLAGS = "resultFlags";

    private static final String STATUS = "status";
    private static final String PROVIDER = "provider";

    public static final String URI = "uri";
    private static final String GALLERY = "gallery";
    private static final String SOURCE_ID = "sourceId";

    private static final String TAGS = "tags";
    private static final String APPLICATION = "application";
    private static final String ASKMD = "askmd";
    private static final String LOCATION_QUERY = "locationQuery";
    private static final String CREDENTIALS = "credentials";
    private static final String MD_DDS = "MD,DDS";
    private static final String SPECIALTIES = "specialties";
    private static final String INSURANCE_PLANS = "insurancePlans";
    private static final String AVATAR_URI = "avatarUri";
    private static final String COUNTRY = "country";
    private static final String DISTANCE = "distance";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String MIDDLE_INITIAL = "middleInitial";
    private static final String PRACTICE_NAME = "practiceName";
    private static final String STREET_ADDRESS_ONE = "streetAddress1";
    private static final String STREET_ADDRESS_TWO = "streetAddress2";
    private static final String SUFFIX = "suffix";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String LOCATION = "location";
    private static final String CPNUM = "cpnum";
    private static final String HOSPITAL_EXCLAMATION = "hospital!";
    public static final String HOSPITAL = "hospital";
    private static final String STREET = "street";
    private static final String GEOLOCATION = "geoLocation";

    // Error
    private static final String SERVICECLIENT_ERROR_KEY = "error";
    private static final String SERVICECLIENT_ERROR_DESCRIPTION_KEY =
            "error_description";
    private static final String SERVICECLIENT_ERROR_MESSAGE_KEY =
            "errorMessage";
    private static final String ERROR_MESSAGE =
            "%s failed with status code %s and message %s";

    private SharecareToken mSharecareToken;
    private boolean reloadTerms;
//    private static SharecareClient _sharecareInstance;

    static
    {
//        switch (AskMDApplication.target)
//        {
//            case STAGING:
//                DATA_SERVICE_URL = "https://data.stage.sharecare.com";
//                AUTH_SERVICE_URL = "https://auth.stage.sharecare.com";
//                break;
//            case QA:
//                DATA_SERVICE_URL = "https://data.mservices.sharecare.com";
//                AUTH_SERVICE_URL = "https://auth.mservices.sharecare.com";
//                break;
//            case PRODUCTION:
//            default:
//                DATA_SERVICE_URL = "https://data.sharecare.com";
//                AUTH_SERVICE_URL = "https://auth.sharecare.com";
//                break;
//        }

        DATA_SERVICE_URL = "https://data.sharecare.com";
        AUTH_SERVICE_URL = "https://auth.sharecare.com";

        CLIENT_ID = "askmd-mobile";
        CLIENT_SECRET = "u7#kl91hkg0";

        REGISTER_ENDPOINT = AUTH_SERVICE_URL + "/account/";
        LOGIN_ENDPOINT = AUTH_SERVICE_URL + "/access/";
        REFRESH_TOKEN_ENDPOINT = AUTH_SERVICE_URL + "/access/";

        FORGOT_PASSWORD_ENDPOINT =
                AUTH_SERVICE_URL + "/account/%s/passwordreset";
        CHANGE_PASSWORD_ENDPOINT = AUTH_SERVICE_URL + "/account/%s/password";

        ASKMD_PROFILE_ENDPOINT = DATA_SERVICE_URL + "/user/%s/profile/AskMD";
        PROFILE_URI_ENDPOINT = DATA_SERVICE_URL + "/user/%s";
        PROFILE_ENDPOINT = AUTH_SERVICE_URL + "/account/%s";
        UPDATE_PROFILE_ENDPOINT = AUTH_SERVICE_URL + "/account/%s";
        MEDICAL_INFO_ENDPOINT = DATA_SERVICE_URL + "/user/%s/medicalinfo";

        ADD_FAMILY_ENDPOINT = DATA_SERVICE_URL + "/user/%s/family";
        EDIT_FAMILY_ENDPOINT = DATA_SERVICE_URL + "/user/%s/family/%s";
        DELETE_FAMILY_ENDPOINT = DATA_SERVICE_URL + "/user/%s/family/%s";

        ALL_CONSULTS_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/documents?includeDocument=false";
        DELETE_CONSULT_ENDPOINT = DATA_SERVICE_URL + "/user/%s/documents/%s";
        GET_CONSULTS_ENDPOINT =
                DATA_SERVICE_URL
                        + "/user/%s/documents?includeDocument=false&metadata&type=userData";
        GET_SESSION_DOCUMENT_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/documents/%s";
        EDIT_CONSULT_ENDPOINT = DATA_SERVICE_URL + "/user/%s/documents/%s";
        ADD_DOCUMENT_ENDPOINT = DATA_SERVICE_URL + "/user/%s/documents";

        EMAIL_EXISTS_ENDPOINT = DATA_SERVICE_URL + "/user?email=%s";
        EMAIL_CONSULT_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/consults/%s/viewers";
        PRINT_CONSULT_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/consults/%s/printable";

        GET_ALL_CONSULT_ASSETS_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/consults/%s/assets";
        ADD_CONSULT_ASSET_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/consults/%s/assets";
        DELETE_CONSULT_ASSET_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/consults/%s/assets/%s";

        GET_MEDICATIONS_ENDPOINT = DATA_SERVICE_URL + "/user/%s/medications";
        ADD_MEDICATION_ENDPOINT = DATA_SERVICE_URL + "/user/%s/medications";
        DELETE_MEDICATION_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/medications/%s";

        GET_SECONDARY_MEDICATIONS_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/family/%s/medications";
        ADD_SECONDARY_MEDICATION_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/family/%s/medications";
        DELETE_SECONDARY_MEDICATION_ENDPOINT =
                DATA_SERVICE_URL + "/user/%s/family/%s/medications/%s";

        GET_CONDITIONS_ENDPOINT = DATA_SERVICE_URL + "/conditions";

        GET_SPECIALTIES_ENDPOINT = DATA_SERVICE_URL + "/specialties?cpnum=%s";
        GET_INSURANCE_CARRIERS_ENDPOINT = DATA_SERVICE_URL + "/insurance";
        GET_INSURANCE_PLANS_ENDPOINT = DATA_SERVICE_URL + "/insurance/%s";
        FIND_PHYSICIANS_ENDPOINT = DATA_SERVICE_URL + "/expert";
        FIND_SPONSORED_PHYSICIANS_ENDPOINT = DATA_SERVICE_URL + "/expert";
        GET_HOSPITAL_BY_LOCATION_NAME_ENDPOINT =
                DATA_SERVICE_URL + "/hospital?locationQuery=%s";
        GET_HOSPITAL_BY_LOCATION_ENDPOINT =
                DATA_SERVICE_URL + "/hospital?location=%s,%s";

        // Set the base64 encoded authorization header
        final String clientString = "nc@email.com" + ":" + "password";
        byte[] data = null;
        try
        {
            data = clientString.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex)
        {
//            Crashlytics.logException(ex);
        }
        final String auth = Base64.encodeToString(data, Base64.DEFAULT);

        AUTHORIZATION_HEADER = "Basic " + auth;
    }



    private URL mUrl;

    public HttpConnector() {}

    public String postForResponse(String url) throws Exception {

        constructRequest(LOGIN_ENDPOINT);


//        HttpURLConnection httpConnection = null;
//        String response = "";
//        try {
//            mUrl = new URL(url);
//            httpConnection = (HttpURLConnection) mUrl.openConnection();
//            httpConnection.setRequestMethod("GET");
//            httpConnection.setRequestProperty("Content-Type", "application/json");
//            httpConnection.setRequestProperty("Content-Language", Locale.getDefault().getISO3Language());
//
//            httpConnection.setUseCaches(false);
//            httpConnection.setDoInput(true);
//
//            InputStream inStream = httpConnection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
//
//            String line = "";
//            StringBuilder strBuilder = new StringBuilder(line);
//            while((line = reader.readLine()) != null) {
//                strBuilder.append(line);
//            }
//
//            response = strBuilder.toString();
//            reader.close();
//        } catch (Exception e) {
//            throw e;
//        }
//        finally {
//            if(httpConnection != null) {
//                httpConnection.disconnect();
//            }
//        }
//
//        return response;
        return "";
    }

    private void constructRequest(String url) throws IOException {


//        RestClient _restClient = new RestClient();


       ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//    private ArrayList <NameValuePair> headers;

        HttpGet get;
        HttpParams httpParameters;
        try
        {
            httpParameters = new BasicHttpParams();
//            String auth = android.util.Base64.encodeToString(
//                    (username + ":" + userpwd).getBytes("UTF-8"),
//                    android.util.Base64.NO_WRAP
//            );

            httpParameters.setParameter("Content-Type", "application/json");
            httpParameters.setParameter("grant_type", PASSWORD);

            HttpPost request = new HttpPost(url);
            request.addHeader("Authorization", AUTHORIZATION_HEADER);
                    request.addHeader("Content-Type", "application/json");


            final HashMap<String, Object> body = new HashMap<String, Object>(3);
            body.put(USERNAME, "nc@email.com");
            body.put(PASSWORD, "password");
            body.put(REMEMBER_ME, "false");


            params.add(new BasicNameValuePair("Content", "application/json"));
            params.add(new BasicNameValuePair("grant_type", PASSWORD));
            params.add(new BasicNameValuePair(USERNAME, "nc@email.com"));
            params.add(new BasicNameValuePair(PASSWORD, "password"));
            params.add(new BasicNameValuePair(REMEMBER_ME, "false"));

            request.setEntity(new UrlEncodedFormEntity(params));
//            HttpConnectionParams.setSoTimeout(httpParameters, 30);
            DefaultHttpClient client = new DefaultHttpClient(httpParameters);
            HttpResponse response = client.execute(request);
            String userAuth = EntityUtils.toString(response.getEntity());

            System.out.println("Data. in login.."+userAuth);

//            _restClient.executeRequest(RestClient.RequestMethod.POST, url, httpParameters, httpHeaders, httpBody, userName, password);


        }

        catch(Exception e)
        {

            System.out.println("Error.."+e);
        }





        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/json");

        // Create request parameters.
        final HashMap<String, String> parameters =
                new HashMap<String, String>(1);
        parameters.put("grant_type", PASSWORD);

        // Create request body.
        final HashMap<String, Object> body = new HashMap<String, Object>(3);
        body.put(USERNAME, "nc@email.com");
        body.put(PASSWORD, "password");
        body.put(REMEMBER_ME, "false");



//        url += "?"

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);



//        URLConnection connection = new URL(url).openConnection();

        String charset = "UTF-8";
        String _request = "";
        String query = "";
        Set<String> keys = parameters.keySet();
        for( String k : keys) {

            query += String.format("%s=%s&", k, parameters.get(k));

        }
        url += "?" + query;



        HttpURLConnection _urlConnection = (HttpURLConnection) new URL(url).openConnection();
        _urlConnection.setRequestMethod("POST");
        _urlConnection.setRequestProperty("Content-Type", "application/json");
        _urlConnection.setRequestProperty("Authorization", AUTHORIZATION_HEADER);
//        Map<String, List<String>> headerFields = _urlConnection.getHeaderFields();

        InputStream _response;

//        _urlConnection.setDoInput(true);
        _urlConnection.setDoOutput(true); // Triggers POST.
//        _urlConnection.setRequestProperty("Accept-Charset", charset);
//        _urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
//        _urlConnection.setRequestProperty("Content-Type", "application/json");

//        _urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
//        _urlConnection.setRequestProperty("Accept","*/*");


//        HttpConnection httpConnection

        OutputStreamWriter wr= new OutputStreamWriter(_urlConnection.getOutputStream());
        wr.write(bodyJson);
        wr.flush();

//        InputStream str = ((HttpsURLConnection) _urlConnection).getErrorStream();

        InputStream inStream = _urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String line = "";
        StringBuilder strBuilder = new StringBuilder(line);
        while((line = reader.readLine()) != null) {
            strBuilder.append(line);
        }


        Object obj = _urlConnection.getContent();

//        _response = _urlConnection.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(_response));

//        String line = "";
//        StringBuilder strBuilder = new StringBuilder(line);
//        while((line = reader.readLine()) != null) {
//            strBuilder.append(line);
//        }

//        String response = strBuilder.toString();
//        System.out.println("GOT RESPONSE: " + response);

        wr.close();
    }
}
