//package mobile.forged.com.health.services;
//
//import android.content.Intent;
//import android.os.Message;
//import android.util.Base64;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import mobile.forged.com.health.networking.HttpConnector;
//
///**
// * Created by visitor15 on 11/10/14.
// */
//public class SharecareService extends BasicService {
//
//    private HttpConnector httpConnector;
//
//    private enum AssetSize
//    {
//        Size300x300,
//        Size240x240,
//        Size120x120,
//        Size290x290,
//        Size298x248,
//        Size60x60
//    }
//
//    private static final int TIME_LIMIT_IN_MINUTES = 60;
//
//    private static final String SERVICE_CLIENT_VERSION = "1.06";
//    private static final String OLDEST_SUPPORTED_VERSION = "1.06";
//
//    private static final String DATA_SERVICE_URL;
//    private static final String AUTH_SERVICE_URL;
//    private static final String CLIENT_ID;
//    private static final String CLIENT_SECRET;
//
//    // Authorization endpoints
//    private static final String AUTHORIZATION_HEADER;
//    private static final String REGISTER_ENDPOINT;
//    private static final String LOGIN_ENDPOINT;
//    private static final String REFRESH_TOKEN_ENDPOINT;
//
//    // Password endpoints
//    private static final String FORGOT_PASSWORD_ENDPOINT;
//    private static final String CHANGE_PASSWORD_ENDPOINT;
//
//    // Profile endpoints
//    private static final String ASKMD_PROFILE_ENDPOINT;
//    private static final String PROFILE_URI_ENDPOINT;
//    private static final String PROFILE_ENDPOINT;
//    private static final String UPDATE_PROFILE_ENDPOINT;
//    private static final String MEDICAL_INFO_ENDPOINT;
//
//    // Secondary profile endpoints
//    private static final String ADD_FAMILY_ENDPOINT;
//    private static final String EDIT_FAMILY_ENDPOINT;
//    private static final String DELETE_FAMILY_ENDPOINT;
//
//    // Consult endpoints
//    private static final String ALL_CONSULTS_ENDPOINT;
//    private static final String DELETE_CONSULT_ENDPOINT;
//    private static final String GET_CONSULTS_ENDPOINT;
//    private static final String GET_SESSION_DOCUMENT_ENDPOINT;
//    private static final String EDIT_CONSULT_ENDPOINT;
//    private static final String ADD_DOCUMENT_ENDPOINT;
//
//    // Share endpoints
//    private static final String EMAIL_EXISTS_ENDPOINT;
//    private static final String EMAIL_CONSULT_ENDPOINT;
//    private static final String PRINT_CONSULT_ENDPOINT;
//
//    // Consult asset endpoints
//    private static final String GET_ALL_CONSULT_ASSETS_ENDPOINT;
//    private static final String ADD_CONSULT_ASSET_ENDPOINT;
//    private static final String DELETE_CONSULT_ASSET_ENDPOINT;
//
//    // Medication endpoints
//    private static final String GET_MEDICATIONS_ENDPOINT;
//    private static final String ADD_MEDICATION_ENDPOINT;
//    private static final String DELETE_MEDICATION_ENDPOINT;
//
//    // Secondary medication endpoints
//    private static final String GET_SECONDARY_MEDICATIONS_ENDPOINT;
//    private static final String ADD_SECONDARY_MEDICATION_ENDPOINT;
//    private static final String DELETE_SECONDARY_MEDICATION_ENDPOINT;
//
//    // Condition endpoints
//    private static final String GET_CONDITIONS_ENDPOINT;
//
//    // Physician endpoints
//    private static final String GET_SPECIALTIES_ENDPOINT;
//    private static final String GET_INSURANCE_CARRIERS_ENDPOINT;
//    private static final String GET_INSURANCE_PLANS_ENDPOINT;
//    private static final String FIND_PHYSICIANS_ENDPOINT;
//    private static final String FIND_SPONSORED_PHYSICIANS_ENDPOINT;
//    private static final String GET_HOSPITAL_BY_LOCATION_NAME_ENDPOINT;
//    private static final String GET_HOSPITAL_BY_LOCATION_ENDPOINT;
//
//    // Json keys
//    private static final String EMAIL = "email";
//    private static final String USERNAME = "username";
//    private static final String PASSWORD = "password";
//    private static final String OLD_PASSWORD = "oldPassword";
//    private static final String NEW_PASSWORD = "newPassword";
//    private static final String REMEMBER_ME = "rememberMe";
//    private static final String HAS_ACCEPTED_TERMS_AND_CONDITIONS =
//            "hasAcceptedTermsAndConditions";
//
//    public static final String PROFILE = "profile";
//    public static final String ID = "id";
//    private static final String FIRST_NAME = "firstName";
//    private static final String LAST_NAME = "lastName";
//    private static final String GENDER = "gender";
//    private static final String DATE_OF_BIRTH = "dateOfBirth";
//    private static final String HEIGHT = "height";
//    private static final String WEIGHT = "weight";
//    private static final String IMAGE = "image";
//    private static final String VIDEO = "video";
//    private static final String TYPE = "type";
//    public static final String URL = "url";
//    public static final String AVATAR = "avatar";
//    private static final String DESCRIPTION = "description";
//    private static final String PROFILE_AVATAR = "ProfileAvatar";
//    private static final String LOCATIONS = "locations";
//
//    private static final String PHYSICIAN = "physician";
//    private static final String NAME = "name";
//    private static final String SPECIALTY = "specialty";
//    private static final String ADDRESS = "address";
//    private static final String CITY = "city";
//    private static final String STATE = "state";
//    private static final String ZIP = "zip";
//    private static final String PHONE = "phone";
//
//    private static final String INSURANCE = "insurance";
//    private static final String MEMBER_ID = "memberId";
//    private static final String GROUP_ID = "groupId";
//    private static final String PLAN = "plan";
//    private static final String PLAN_ID = "planId";
//    private static final String PLAN_NAME = "planName";
//    private static final String CARRIER_ID = "carrierId";
//    private static final String CARRIER_NAME = "carrierName";
//
//    private static final String VITAL_STATS = "vitalStatistics";
//    private static final String CHOLESTEROL = "cholesterol";
//    private static final String TOTAL = "total";
//    private static final String LDL = "ldl";
//    private static final String HDL = "hdl";
//    private static final String BLOOD_PRESSURE = "bloodPressure";
//    private static final String SYSTOLIC = "systolic";
//    private static final String DIASTOLIC = "diastolic";
//    public static final String MEDICAL_CONDITIONS = "medicalConditions";
//    private static final String TRIGLYCERIDES = "triglycerides";
//    private static final String TIME_SINCE_LAST_VISIT = "timeSinceLastVisit";
//    private static final String THIS_MONTH = "THIS_MONTH";
//    private static final String WITHIN_ONE_YEAR = "WITHIN_ONE_YEAR";
//    private static final String ONE_TWO_YEARS = "ONE_TWO_YEARS";
//    private static final String THREE_FIVE_YEARS = "THREE_FIVE_YEARS";
//    private static final String FIVE_PLUS_YEARS = "FIVE_PLUS_YEARS";
//    private static final String DONT_GO = "DONT_GO";
//
//    private static final String TITLE = "title";
//    private static final String METADATA = "metadata";
//    private static final String VERSION = "version";
//    public static final String DOCUMENT = "document";
//    private static final String DELETED = "deleted";
//    private static final String SESSION = "session";
//    private static final String SESSION_DOC_ID = "sessionDocID";
//    private static final String FND_LIST = "fndList";
//    private static final String FND_LIST_DOC_ID = "fndListDocID";
//    private static final String USER_DATA = "userData";
//    private static final String USER_DATA_PRIVATE = "userDataPrivate";
//    private static final String USER_DATA_PRIVATE_DOC_ID =
//            "userDataPrivateDocID";
//    private static final String POPT_LIST = "poptList";
//    private static final String POPT_LIST_DOC_ID = "poptListDocID";
//    private static final String DATE_ADDED = "dateAdded";
//    private static final String CATEGORY = "category";
//    private static final String TOPIC_ID = "topicID";
//    private static final String INFOCARD_ID = "infocardID";
//    private static final String REV = "rev";
//    private static final String PROFILE_ID = "profileID";
//    private static final String NUM_CAUSES_FLAGGED = "numCausesFlagged";
//    private static final String NUM_PHYSICIANS_CALLED = "numPhysiciansCalled";
//    private static final String HAS_NOTE = "hasNote";
//    private static final String PHYSICIANS_CALLED = "physiciansCalled";
//    private static final String CHECKED_MEDS = "checkedMeds";
//    private static final String NOTES = "notes";
//    private static final String MARKED_INDICES = "markedIndices";
//    private static final String RESULT_FLAGS = "resultFlags";
//
//    private static final String STATUS = "status";
//    private static final String PROVIDER = "provider";
//
//    public static final String URI = "uri";
//    private static final String GALLERY = "gallery";
//    private static final String SOURCE_ID = "sourceId";
//
//    private static final String TAGS = "tags";
//    private static final String APPLICATION = "application";
//    private static final String ASKMD = "askmd";
//    private static final String LOCATION_QUERY = "locationQuery";
//    private static final String CREDENTIALS = "credentials";
//    private static final String MD_DDS = "MD,DDS";
//    private static final String SPECIALTIES = "specialties";
//    private static final String INSURANCE_PLANS = "insurancePlans";
//    private static final String AVATAR_URI = "avatarUri";
//    private static final String COUNTRY = "country";
//    private static final String DISTANCE = "distance";
//    private static final String LAT = "lat";
//    private static final String LNG = "lng";
//    private static final String MIDDLE_INITIAL = "middleInitial";
//    private static final String PRACTICE_NAME = "practiceName";
//    private static final String STREET_ADDRESS_ONE = "streetAddress1";
//    private static final String STREET_ADDRESS_TWO = "streetAddress2";
//    private static final String SUFFIX = "suffix";
//    private static final String PHONE_NUMBER = "phoneNumber";
//    private static final String LOCATION = "location";
//    private static final String CPNUM = "cpnum";
//    private static final String HOSPITAL_EXCLAMATION = "hospital!";
//    public static final String HOSPITAL = "hospital";
//    private static final String STREET = "street";
//    private static final String GEOLOCATION = "geoLocation";
//
//    // Error
//    private static final String SERVICECLIENT_ERROR_KEY = "error";
//    private static final String SERVICECLIENT_ERROR_DESCRIPTION_KEY =
//            "error_description";
//    private static final String SERVICECLIENT_ERROR_MESSAGE_KEY =
//            "errorMessage";
//    private static final String ERROR_MESSAGE =
//            "%s failed with status code %s and message %s";
//
//    private SharecareToken mSharecareToken;
//    private boolean reloadTerms;
//    private static SharecareClient _sharecareInstance;
//
//    static
//    {
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
//
//        CLIENT_ID = "askmd-mobile";
//        CLIENT_SECRET = "u7#kl91hkg0";
//
//        REGISTER_ENDPOINT = AUTH_SERVICE_URL + "/account/";
//        LOGIN_ENDPOINT = AUTH_SERVICE_URL + "/access/";
//        REFRESH_TOKEN_ENDPOINT = AUTH_SERVICE_URL + "/access/";
//
//        FORGOT_PASSWORD_ENDPOINT =
//                AUTH_SERVICE_URL + "/account/%s/passwordreset";
//        CHANGE_PASSWORD_ENDPOINT = AUTH_SERVICE_URL + "/account/%s/password";
//
//        ASKMD_PROFILE_ENDPOINT = DATA_SERVICE_URL + "/user/%s/profile/AskMD";
//        PROFILE_URI_ENDPOINT = DATA_SERVICE_URL + "/user/%s";
//        PROFILE_ENDPOINT = AUTH_SERVICE_URL + "/account/%s";
//        UPDATE_PROFILE_ENDPOINT = AUTH_SERVICE_URL + "/account/%s";
//        MEDICAL_INFO_ENDPOINT = DATA_SERVICE_URL + "/user/%s/medicalinfo";
//
//        ADD_FAMILY_ENDPOINT = DATA_SERVICE_URL + "/user/%s/family";
//        EDIT_FAMILY_ENDPOINT = DATA_SERVICE_URL + "/user/%s/family/%s";
//        DELETE_FAMILY_ENDPOINT = DATA_SERVICE_URL + "/user/%s/family/%s";
//
//        ALL_CONSULTS_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/documents?includeDocument=false";
//        DELETE_CONSULT_ENDPOINT = DATA_SERVICE_URL + "/user/%s/documents/%s";
//        GET_CONSULTS_ENDPOINT =
//                DATA_SERVICE_URL
//                        + "/user/%s/documents?includeDocument=false&metadata&type=userData";
//        GET_SESSION_DOCUMENT_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/documents/%s";
//        EDIT_CONSULT_ENDPOINT = DATA_SERVICE_URL + "/user/%s/documents/%s";
//        ADD_DOCUMENT_ENDPOINT = DATA_SERVICE_URL + "/user/%s/documents";
//
//        EMAIL_EXISTS_ENDPOINT = DATA_SERVICE_URL + "/user?email=%s";
//        EMAIL_CONSULT_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/consults/%s/viewers";
//        PRINT_CONSULT_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/consults/%s/printable";
//
//        GET_ALL_CONSULT_ASSETS_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/consults/%s/assets";
//        ADD_CONSULT_ASSET_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/consults/%s/assets";
//        DELETE_CONSULT_ASSET_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/consults/%s/assets/%s";
//
//        GET_MEDICATIONS_ENDPOINT = DATA_SERVICE_URL + "/user/%s/medications";
//        ADD_MEDICATION_ENDPOINT = DATA_SERVICE_URL + "/user/%s/medications";
//        DELETE_MEDICATION_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/medications/%s";
//
//        GET_SECONDARY_MEDICATIONS_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/family/%s/medications";
//        ADD_SECONDARY_MEDICATION_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/family/%s/medications";
//        DELETE_SECONDARY_MEDICATION_ENDPOINT =
//                DATA_SERVICE_URL + "/user/%s/family/%s/medications/%s";
//
//        GET_CONDITIONS_ENDPOINT = DATA_SERVICE_URL + "/conditions";
//
//        GET_SPECIALTIES_ENDPOINT = DATA_SERVICE_URL + "/specialties?cpnum=%s";
//        GET_INSURANCE_CARRIERS_ENDPOINT = DATA_SERVICE_URL + "/insurance";
//        GET_INSURANCE_PLANS_ENDPOINT = DATA_SERVICE_URL + "/insurance/%s";
//        FIND_PHYSICIANS_ENDPOINT = DATA_SERVICE_URL + "/expert";
//        FIND_SPONSORED_PHYSICIANS_ENDPOINT = DATA_SERVICE_URL + "/expert";
//        GET_HOSPITAL_BY_LOCATION_NAME_ENDPOINT =
//                DATA_SERVICE_URL + "/hospital?locationQuery=%s";
//        GET_HOSPITAL_BY_LOCATION_ENDPOINT =
//                DATA_SERVICE_URL + "/hospital?location=%s,%s";
//
//        // Set the base64 encoded authorization header
//        final String clientString = CLIENT_ID + ":" + CLIENT_SECRET;
//        byte[] data = null;
//        try
//        {
//            data = clientString.getBytes("UTF-8");
//        }
//        catch (final UnsupportedEncodingException ex)
//        {
////            Crashlytics.logException(ex);
//        }
//        final String auth = Base64.encodeToString(data, Base64.DEFAULT);
//
//        AUTHORIZATION_HEADER = "Basic " + auth;
//    }
//
//    /**
//     * Attempt to login.
//     *
//     * @param email
//     * @param password
//     * @completion indicates request success or error message.
//     */
//    public void loginWithEmail(final String email, final String password,
//                               final ServiceClientCompletion<ResponseResult> completion)
//    {
//        // Validate email format.
//        if (!isEmailFormat(email) && completion != null)
//        {
//            final ResponseResult result =
//                    new ResponseResult(
//                            false,
//                            "Please enter a valid email.",
//                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS);
//            completion
//                    .onCompletion(
//                            ServiceResultStatus.FAILED,
//                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS,
//                            result);
//            return;
//        }
//
//        // Create request headers.
//        final HashMap<String, String> headers = new HashMap<String, String>(2);
//        headers.put("Authorization", AUTHORIZATION_HEADER);
//        headers.put("Content-Type", "application/json");
//
//        // Create request parameters.
//        final HashMap<String, String> parameters =
//                new HashMap<String, String>(1);
//        parameters.put("grant_type", PASSWORD);
//
//        // Create request body.
//        final HashMap<String, Object> body = new HashMap<String, Object>(3);
//        body.put(USERNAME, email);
//        body.put(PASSWORD, password);
//        body.put(REMEMBER_ME, "true");
//
//        final Gson gson = new GsonBuilder().create();
//        final String bodyJson = gson.toJson(body);
//        this.beginRequest(LOGIN_ENDPOINT, ServiceMethod.POST, headers,
//                parameters, bodyJson, ServiceResponseFormat.GSON,
//                new ServiceResponseTransform<JsonElement, ResponseResult>()
//                {
//                    @Override
//                    public ResponseResult transformResponseData(
//                            final JsonElement json)
//                            throws ServiceResponseTransformException
//                    {
//                        final ResponseResult result =
//                                checkResultFromAuthService(json);
//                        if (result.success)
//                        {
//                            final boolean success =
//                                    setSharecareToken(json, false, false);
//                            if (!success)
//                            {
//                                result.responseCode =
//                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
//                            }
//                        }
//
//                        switch (result.responseCode)
//                        {
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA:
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR:
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_NONE:
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN:
//                                result.success = false;
//                                result.errorMessage =
//                                        "We're sorry. Something went wrong. Please try again.";
//                                break;
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNAUTHORIZED:
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS:
//                                result.success = false;
//                                result.errorMessage =
//                                        "The email address or password you entered did not match our records.";
//                                break;
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SUCCESS:
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_CANCELLED:
//                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_ALERT_MESSAGE:
//                                result.errorMessage = "";
//                                break;
//                        }
//
//                        LogError("loginWithEmail", result);
//                        return result;
//                    }
//                }, new ServiceClientCompletion<ResponseResult>()
//                {
//                    @Override
//                    public void onCompletion(
//                            final ServiceResultStatus serviceResultStatus,
//                            final int responseCode, ResponseResult resultValue)
//                    {
//                        if (completion != null)
//                        {
//                            if (responseCode == ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNAUTHORIZED
//                                    || responseCode == ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_NOT_FOUND)
//                            {
//                                // Handle 401 error.
//                                resultValue =
//                                        new ResponseResult(
//                                                false,
//                                                "The email address or password you entered did not match our records.",
//                                                responseCode);
//                            }
//                            completion.onCompletion(serviceResultStatus,
//                                    responseCode, resultValue);
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public <TResponse, TResult> ServiceOperation<TResponse, TResult> beginRequest(
//            final String uri, final ServiceMethod method,
//            final Map<String, String> headers,
//            final Map<String, String> queryParameters,
//            final BodyDataProvider bodyDataProvider,
//            final ServiceResponseFormat<TResponse> responseFormat,
//            final ServiceResponseTransform<TResponse, TResult> responseTransform,
//            final ServiceClientCompletion<TResult> completion,
//            final ServiceOperationPriority priority, final boolean useCaches)
//    {
//        final boolean headerContainsAccessToken =
//                headers.get("Authorization").contains("SSO");
//        final boolean paramsContainsAccessToken =
//                queryParameters != null
//                        && queryParameters.containsKey("access_token");
//        if (headerContainsAccessToken || paramsContainsAccessToken)
//        {
//            final long timeDiff =
//                    mSharecareToken.expiresIn.getTime() - new Date().getTime();
//            long timeDiffInMin = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
//            timeDiffInMin = 0;
//            if (timeDiffInMin < TIME_LIMIT_IN_MINUTES)
//            {
//                final ServiceOperation<TResponse, TResult> serviceOperation =
//                        new ServiceOperation<TResponse, TResult>(uri, method,
//                                headers, queryParameters, bodyDataProvider,
//                                responseFormat, responseTransform, completion,
//                                priority, useCaches,
//                                (int)getRequestTimeoutInMilliseconds(), this);
//
//                refreshToken(new ServiceClientCompletion<ResponseResult>()
//                {
//
//                    @Override
//                    public void onCompletion(
//                            final ServiceResultStatus serviceResultStatus,
//                            final int responseCode, final ResponseResult resultValue)
//                    {
//                        if (serviceResultStatus == ServiceResultStatus.SUCCESS)
//                        {
//                            final String accessToken =
//                                    mSharecareToken.accessToken;
//                            if (paramsContainsAccessToken)
//                                queryParameters
//                                        .put("access_token", accessToken);
//                            if (headerContainsAccessToken)
//                                headers.put("Authorization",
//                                        String.format("SSO %s", accessToken));
//                            SharecareClient.this.getRequestPool().execute(
//                                    serviceOperation);
//                        }
//                        else
//                        {
//                            SharecareClient.this.getRequestPool().execute(
//                                    serviceOperation);
//                        }
//                    }
//                });
//
//                return serviceOperation;
//            }
//            else
//            {
//                return super.beginRequest(uri, method, headers,
//                        queryParameters, bodyDataProvider, responseFormat,
//                        responseTransform, completion, priority, useCaches);
//            }
//        }
//        else
//        {
//            return super.beginRequest(uri, method, headers, queryParameters,
//                    bodyDataProvider, responseFormat, responseTransform,
//                    completion, priority, useCaches);
//        }
//    }
//
//    /**
//     * Attempt to refresh the accessToken.
//     *
//     * @param completion
//     *            the new access token is saved in the ResponseResult
//     *            parameters, under the "newAccessToken" key.
//     */
//    public void refreshToken(
//            final ServiceClientCompletion<ResponseResult> completion)
//    {
//        // Create request headers.
//        final HashMap<String, String> headers = new HashMap<String, String>(2);
//        headers.put("Authorization", AUTHORIZATION_HEADER);
//        headers.put("Content-Type", "application/x-www-form-urlencoded");
//
//        // Create request parameters.
//        final HashMap<String, String> parameters =
//                new HashMap<String, String>(2);
//        parameters.put("grant_type", "refresh_token");
//        parameters.put("refresh_token", mSharecareToken.refreshToken);
//
//        this.beginRequest(REFRESH_TOKEN_ENDPOINT, ServiceMethod.GET, headers,
//                parameters, (String)null, ServiceResponseFormat.GSON,
//                new ServiceResponseTransform<JsonElement, ResponseResult>()
//                {
//                    @Override
//                    public ResponseResult transformResponseData(
//                            final JsonElement json)
//                            throws ServiceResponseTransformException
//                    {
//                        final ResponseResult result =
//                                checkResultFromAuthService(json);
//                        if (result.success)
//                        {
//                            final boolean success =
//                                    setSharecareToken(json,
//                                            mSharecareToken.askMDProfileCreated,
//                                            mSharecareToken.preProfileCreation);
//                            if (success)
//                            {
//                                final HashMap<String, Object> parameters =
//                                        new HashMap<String, Object>(1);
//                                parameters.put("newAccessToken",
//                                        mSharecareToken.accessToken);
//                                result.parameters = parameters;
//                            }
//                        }
//
//                        LogError("refreshToken", result);
//                        return result;
//                    }
//                }, completion);
//    }
//
//    /**
//     * Attempt to retrieve the user's profile values.
//     *
//     * @param completion
//     *            if successful, the profile values are stored in the
//     *            ResponseResult's parameters property, under the key "profile".
//     */
//    public void getProfileWithCompletion(
//            final ServiceClientCompletion<ResponseResult> completion)
//    {
//        // Create request headers.
//        final HashMap<String, String> headers = new HashMap<String, String>(2);
//        headers.put("Authorization", AUTHORIZATION_HEADER);
//        headers.put("Content-Type", "application/json");
//
//        // Create request parameters.
//        final HashMap<String, String> parameters =
//                new HashMap<String, String>(2);
//        parameters.put("grant_type", "bearer");
//        parameters.put("access_token", mSharecareToken.accessToken);
//
//        // Create endpoint.
//        final String endPoint =
//                String.format(PROFILE_ENDPOINT, mSharecareToken.accountID);
//
//        this.beginRequest(endPoint, ServiceMethod.GET, headers, parameters,
//                (String)null, ServiceResponseFormat.GSON,
//                new ServiceResponseTransform<JsonElement, ResponseResult>()
//                {
//                    @Override
//                    public ResponseResult transformResponseData(
//                            final JsonElement json)
//                            throws ServiceResponseTransformException
//                    {
//                        final ResponseResult result =
//                                checkResultFromAuthService(json);
//                        if (result.success)
//                        {
//                            // Extract profile info to create profile object.
//                            final JsonObject jsonObject = json.getAsJsonObject();
//                            final String firstName =
//                                    getStringFromJson(jsonObject, FIRST_NAME);
//                            final String lastName =
//                                    getStringFromJson(jsonObject, LAST_NAME);
//                            final String email =
//                                    getStringFromJson(jsonObject, EMAIL);
//                            final String gender =
//                                    getStringFromJson(jsonObject, GENDER);
//                            final Date dateOfBirth =
//                                    getDateFromJson(jsonObject, DATE_OF_BIRTH);
//                            final double height =
//                                    getDoubleFromJson(jsonObject, HEIGHT, 0);
//                            final double weight =
//                                    getDoubleFromJson(jsonObject, WEIGHT, 0);
//
//                            if (firstName != null && lastName != null
//                                    && email != null)
//                            {
//                                final Profile profile = new Profile();
//                                profile.firstName = firstName;
//                                profile.lastName = lastName;
//                                profile.email = email;
//                                profile.heightInMeters = height;
//                                profile.weightInKg = weight;
//                                profile.gender = gender;
//                                profile.dateOfBirth = dateOfBirth;
//                                updateAskMDProfileWithCompletion(null);
//                                final HashMap<String, Object> parameters =
//                                        new HashMap<String, Object>(1);
//                                parameters.put(PROFILE, profile);
//                                result.parameters = parameters;
//                            }
//                            else
//                            {
//                                result.success = false;
//                                result.errorMessage = "Bad profile data.";
//                                result.responseCode =
//                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
//                            }
//                        }
//
//                        LogError("getProfileWithCompletion", result);
//                        return result;
//                    }
//                }, completion);
//    }
//
//    @Override
//    protected void onHandleServiceMessage(Message msg) {
//
//    }
//
//    @Override
//    protected void onHandleWorkerMessage(Message msg) {
//
//    }
//
//    @Override
//    public void onDestroy() {
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return 0;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return false;
//    }
//
//    @Override
//    public void onRebind(Intent intent) {
//
//    }
//
//    private void constructRequest(String url) throws IOException {
//
//        // Create request headers.
//        final HashMap<String, String> headers = new HashMap<String, String>(2);
//        headers.put("Authorization", AUTHORIZATION_HEADER);
//        headers.put("Content-Type", "application/json");
//
//        // Create request parameters.
//        final HashMap<String, String> parameters =
//                new HashMap<String, String>(1);
//        parameters.put("grant_type", PASSWORD);
//
//        // Create request body.
//        final HashMap<String, Object> body = new HashMap<String, Object>(3);
//        body.put(USERNAME, "visitor15@gmail.com");
//        body.put(PASSWORD, "tatsu138");
//        body.put(REMEMBER_ME, "true");
//
//        final Gson gson = new GsonBuilder().create();
//        final String bodyJson = gson.toJson(body);
//
//        URLConnection connection = new URL(url).openConnection();
//
//        String charset = "UTF-8";
//        String _request = "";
//
////        String query = String.format("param1=%s&param2=%s",
////                URLEncoder.encode(param1, charset),
////                URLEncoder.encode(param2, charset));
//
//        URLConnection _urlConnection = new URL(url).openConnection();
//
//        InputStream _response;
//
//        _urlConnection.setDoOutput(true); // Triggers POST.
//        _urlConnection.setRequestProperty("Accept-Charset", charset);
////        _urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
//        _urlConnection.setRequestProperty("Content-Type", "application/json");
//
//        OutputStreamWriter wr= new OutputStreamWriter(_urlConnection.getOutputStream());
//        wr.write(bodyJson);
//        wr.flush();
//        wr.close();
//
//        _response = connection.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
//
//        String line = "";
//        StringBuilder strBuilder = new StringBuilder(line);
//        while((line = reader.readLine()) != null) {
//            strBuilder.append(line);
//        }
//
//        String response = strBuilder.toString();
//        System.out.println("GOT RESPONSE: " + response);
//    }
//}
