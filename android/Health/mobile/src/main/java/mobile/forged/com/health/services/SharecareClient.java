package mobile.forged.com.health.services;

/**
* Created by visitor15 on 11/9/14.
*/

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nascentdigital.communication.BodyDataProvider;
import com.nascentdigital.communication.ServiceClient;
import com.nascentdigital.communication.ServiceClientCompletion;
import com.nascentdigital.communication.ServiceClientConstants;
import com.nascentdigital.communication.ServiceMethod;
import com.nascentdigital.communication.ServiceOperation;
import com.nascentdigital.communication.ServiceOperationPriority;
import com.nascentdigital.communication.ServiceResponseFormat;
import com.nascentdigital.communication.ServiceResponseTransform;
import com.nascentdigital.communication.ServiceResponseTransformException;
import com.nascentdigital.communication.ServiceResultStatus;
import com.nascentdigital.util.observing.ObservableArrayList;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import mobile.forged.com.health.consultation.Asset;
import mobile.forged.com.health.consultation.Asset.AssetType;
import mobile.forged.com.health.consultation.ConsultSummary;
import mobile.forged.com.health.consultation.ConsultSummary.InvalidDocumentTypeException;
import mobile.forged.com.health.consultation.Consultation;
import mobile.forged.com.health.consultation.OfficeLocation;
import mobile.forged.com.health.consultation.Physician;
import mobile.forged.com.health.consultation.Result;
import mobile.forged.com.health.consultation.ResultCategory;
import mobile.forged.com.health.consultation.Specialty;
import mobile.forged.com.health.consultation.SpecialtyTag;
import mobile.forged.com.health.entities.Hospital;
import mobile.forged.com.health.entities.TopicType;
import mobile.forged.com.health.managers.ProfileManager;
import mobile.forged.com.health.managers.SettingsManager;
import mobile.forged.com.health.profile.InsuranceCarrier;
import mobile.forged.com.health.profile.InsurancePlan;
import mobile.forged.com.health.profile.InsurancePlanAndCarrier;
import mobile.forged.com.health.profile.InsuranceProfile;
import mobile.forged.com.health.profile.Medication;
import mobile.forged.com.health.profile.Profile;
import mobile.forged.com.health.profile.ProfilePhysician;
import mobile.forged.com.health.profile.SecondaryProfile;
import mobile.forged.com.health.profile.VitalStats;
import mobile.forged.com.health.profile.VitalStats.TimeSinceLastVisitType;
import mobile.forged.com.health.utilities.KMPHelper;
import mobile.forged.com.health.utilities.StringHelper;
import mobile.forged.com.health.vitals.BloodPressure;
import mobile.forged.com.health.vitals.Cholesterol;
import mobile.forged.com.health.vitals.MedicalCondition;

public class SharecareClient extends ServiceClient
{
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
    private static SharecareClient _sharecareInstance;

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

        DATA_SERVICE_URL = "http://";
        AUTH_SERVICE_URL = "https://auth.sharecare.com";

//        CLIENT_ID = "askmd-mobile";
//        CLIENT_SECRET = "u7#kl91hkg0";

        CLIENT_ID = "testuser@email.com";
        CLIENT_SECRET = "password";

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
        final String clientString = CLIENT_ID + ":" + CLIENT_SECRET;
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

    // [region] public methods

    public static synchronized SharecareClient getSharedInstance()
    {
        if (_sharecareInstance == null)
        {
            _sharecareInstance = new SharecareClient();
        }

        return _sharecareInstance;
    }

    public SharecareToken getSharecareToken()
    {
        return mSharecareToken;
    }

    // [endregion]

    // [region] helper methods

    /**
     * Checks the error messages in a JSON response and returns a ResponseResult
     * object.
     *
     * @param json
     * @return a ResponseResult object representing either a success or a
     *         failure with corresponding error codes and messages.
     */
    public static ResponseResult checkResultFromAuthService(
            final JsonElement json)
    {
        final ResponseResult result = new ResponseResult();
        if (json != null && json.isJsonObject())
        {
            final JsonObject jsonObject = json.getAsJsonObject();
            result.errorMessage =
                    getStringFromJson(jsonObject,
                            SERVICECLIENT_ERROR_DESCRIPTION_KEY);

            String errorCode =
                    getStringFromJson(jsonObject, SERVICECLIENT_ERROR_KEY);
            final String errorMessage =
                    getStringFromJson(jsonObject, SERVICECLIENT_ERROR_MESSAGE_KEY);
            if (errorCode == null)
            {
                errorCode =
                        getStringFromJson(jsonObject,
                                SERVICECLIENT_ERROR_MESSAGE_KEY);
            }

            if (errorCode != null)
            {
                result.success = false;
                errorCode = errorCode.toLowerCase(Locale.US);
                if (errorCode.equals("server_error")
                        || errorCode.equals("temporarily_unavailable"))
                {
                    result.responseCode =
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                }
                else if (errorCode.equals("access_denied")
                        || errorCode.equals("invalid_client")
                        || errorCode.equals("unauthorized_client"))
                {
                    result.responseCode =
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNAUTHORIZED;
                }
                else if (errorCode.equals("invalid_grant")
                        || errorCode.equals("invalid_request")
                        || errorCode.equals("invalid_scope")
                        || errorCode.equals("unsupported_grant_type")
                        || errorCode.equals("unsupported_response_type"))
                {
                    result.responseCode =
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS;
                }
                else
                {
                    result.responseCode =
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN;
                }
            }
            else if (errorMessage != null && errorMessage.equals("FAILURE"))
            {
                result.success = false;
                result.responseCode =
                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
            }
        }
        return result;
    }

    /**
     * Checks the type and result parameters of a data service JSON response to
     * see if it was successful.
     *
     * @param json
     * @return true if successful, false if not.
     */
    private static boolean checkResultFromDataService(final JsonElement json)
    {
        if (json != null && json.isJsonObject())
        {
            final JsonObject jsonObject = json.getAsJsonObject();
            String type = getStringFromJson(jsonObject, TYPE);
            if (!StringHelper.isNullOrEmpty(type))
            {
                type = type.toLowerCase(Locale.US);
            }
            String resultFromJson = getStringFromJson(jsonObject, "result");
            if (!StringHelper.isNullOrEmpty(resultFromJson))
            {
                resultFromJson = resultFromJson.toLowerCase(Locale.US);
            }
            return (type.contains("success") && resultFromJson
                    .equals("success"));
        }
        return false;
    }

    /**
     * If an error message is attached to the result parameter, the method,
     * response code, and error messages are logged.
     *
     * @param method
     *            the method being called.
     * @param result
     *            the ResponseResult object.
     */
    private static void LogError(final String method,
                                 final ResponseResult result)
    {
        if (result != null && !StringHelper.isNullOrEmpty(result.errorMessage))
        {
//            Crashlytics.log("SharecareClient() FAILURE: "
//                    + String.format(ERROR_MESSAGE, method, result.responseCode,
//                    result.errorMessage));
        }
    }

    /**
     * Utility method that returns common header parameters.
     *
     * @param accessToken
     * @return
     */
    private static HashMap<String, String> getHeaderWithAccessToken(
            final String accessToken)
    {
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", String.format("SSO %s", accessToken));
        headers.put("Content-Type", "application/json");
        return headers;
    }


    /**
     * Utility method that checks if a String matches email format.
     *
     * @param str
     * @return true if matching, false if not.
     */
    private static boolean isEmailFormat(final String str)
    {
        return str.matches("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");
    }

    /**
     * Utility method that returns the response value of a JsonObject.
     *
     * @param json
     * @return the value corresponding to the response key if it exists, or null
     *         otherwise.
     */
    public static JsonElement getResponseFromJson(final JsonElement json)
    {
        if (json != null && json.isJsonObject())
        {
            final JsonObject jsonObject = json.getAsJsonObject();
            return jsonObject.get("response");
        }
        return null;
    }

    /**
     * Utility method that returns the corresponding JsonObject for the given
     * key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @return the JsonObject corresponding to the key if it exists, or null
     *         otherwise.
     */
    private static JsonObject getJsonObjectFromJson(
            final JsonObject jsonObject, final String key)
    {
        if (jsonObject != null && jsonObject.get(key) != null
                && jsonObject.get(key).isJsonObject())
        {
            return jsonObject.get(key).getAsJsonObject();
        }
        return null;
    }

    /**
     * Utility method that returns the corresponding JsonArray for the given
     * key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @return the JsonArray corresponding to the key if it exists, or null
     *         otherwise.
     */
    public static JsonArray getJsonArrayFromJson(final JsonObject jsonObject,
                                                 final String key)
    {
        if (jsonObject != null && jsonObject.get(key) != null
                && jsonObject.get(key).isJsonArray())
        {
            return jsonObject.get(key).getAsJsonArray();
        }
        return null;
    }

    /**
     * Utility method that returns the corresponding String for the given key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @return the String corresponding to the key if it exists, or null
     *         otherwise.
     * @throws ClassCastException
     *             if the value corresponding to the key is not a String.
     */
    public static String getStringFromJson(final JsonObject jsonObject,
                                           final String key) throws ClassCastException
    {
        if (jsonObject != null && jsonObject != null
                && jsonObject.get(key) != null && !jsonObject.get(key).isJsonNull())
        {
            return jsonObject.get(key).getAsString();
        }
        return null;
    }

    /**
     * Utility method that returns the corresponding boolean for the given key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @param defaultValue
     * @return the boolean corresponding to the given key if it exists, or the
     *         defaultValue otherwise.
     * @throws ClassCastException
     *             if the value corresponding to the given key is not of type
     *             boolean.
     */
    private static boolean getBooleanFromJson(final JsonObject jsonObject,
                                              final String key, final boolean defaultValue) throws ClassCastException
    {
        if (jsonObject != null && jsonObject.get(key) != null
                && !jsonObject.get(key).isJsonNull())
        {
            return jsonObject.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    /**
     * Utility method that returns the corresponding int for the given key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @param defaultValue
     * @return the int corresponding to the given key if it exists, or the
     *         defaultValue otherwise.
     * @throws ClassCastException
     *             if the value corresponding to the given key is not of type
     *             int.
     */
    private static int getIntFromJson(final JsonObject jsonObject,
                                      final String key, final int defaultValue) throws ClassCastException
    {
        if (jsonObject != null && jsonObject.get(key) != null
                && !jsonObject.get(key).isJsonNull())
        {
            return jsonObject.get(key).getAsInt();
        }
        return defaultValue;
    }

    /**
     * Utility method that returns the corresponding double for the given key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @param defaultValue
     * @return the double corresponding to the given key if it exists, or the
     *         defaultValue otherwise.
     * @throws ClassCastException
     *             if the value corresponding to the given key is not of type
     *             double.
     */
    private static double getDoubleFromJson(final JsonObject jsonObject,
                                            final String key, final double defaultValue) throws ClassCastException
    {
        if (jsonObject != null && jsonObject.get(key) != null
                && !jsonObject.get(key).isJsonNull())
        {
            return jsonObject.get(key).getAsDouble();
        }
        return defaultValue;
    }

    /**
     * Utility method that returns the corresponding Date for the given key.
     *
     * @param jsonObject
     *            the JsonObject to search.
     * @param key
     * @return the Date corresponding to the given key if it exists, or null
     *         otherwise.
     * @throws ClassCastException
     *             if the value corresponding to the given key is not of type
     *             double.
     */
    private static Date getDateFromJson(final JsonObject jsonObject,
                                        final String key) throws ClassCastException
    {
        if (jsonObject != null && jsonObject.get(key) != null
                && !jsonObject.get(key).isJsonNull())
        {
            final double milliseconds = getDoubleFromJson(jsonObject, key, 0);
            return new Date((long)milliseconds);
        }
        return null;
    }

    // [endregion]

    // [region] overridden methods

    @Override
    public <TResponse, TResult> ServiceOperation<TResponse, TResult> beginRequest(
            final String uri, final ServiceMethod method,
            final Map<String, String> headers,
            final Map<String, String> queryParameters,
            final BodyDataProvider bodyDataProvider,
            final ServiceResponseFormat<TResponse> responseFormat,
            final ServiceResponseTransform<TResponse, TResult> responseTransform,
            final ServiceClientCompletion<TResult> completion,
            final ServiceOperationPriority priority, final boolean useCaches)
    {
        final boolean headerContainsAccessToken =
                headers.get("Authorization").contains("SSO");
        final boolean paramsContainsAccessToken =
                queryParameters != null
                        && queryParameters.containsKey("access_token");
        if (headerContainsAccessToken || paramsContainsAccessToken)
        {
            final long timeDiff =
                    mSharecareToken.expiresIn.getTime() - new Date().getTime();
            long timeDiffInMin = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
            timeDiffInMin = 0;
            if (timeDiffInMin < TIME_LIMIT_IN_MINUTES)
            {
                final ServiceOperation<TResponse, TResult> serviceOperation =
                        new ServiceOperation<TResponse, TResult>(uri, method,
                                headers, queryParameters, bodyDataProvider,
                                responseFormat, responseTransform, completion,
                                priority, useCaches,
                                (int)getRequestTimeoutInMilliseconds(), this);

                refreshToken(new ServiceClientCompletion<ResponseResult>()
                {

                    @Override
                    public void onCompletion(
                            final ServiceResultStatus serviceResultStatus,
                            final int responseCode, final ResponseResult resultValue)
                    {
                        if (serviceResultStatus == ServiceResultStatus.SUCCESS)
                        {
                            final String accessToken =
                                    mSharecareToken.accessToken;
                            if (paramsContainsAccessToken)
                                queryParameters
                                        .put("access_token", accessToken);
                            if (headerContainsAccessToken)
                                headers.put("Authorization",
                                        String.format("SSO %s", accessToken));
                            SharecareClient.this.getRequestPool().execute(
                                    serviceOperation);
                        }
                        else
                        {
                            SharecareClient.this.getRequestPool().execute(
                                    serviceOperation);
                        }
                    }
                });

                return serviceOperation;
            }
            else
            {
                return super.beginRequest(uri, method, headers,
                        queryParameters, bodyDataProvider, responseFormat,
                        responseTransform, completion, priority, useCaches);
            }
        }
        else
        {
            return super.beginRequest(uri, method, headers, queryParameters,
                    bodyDataProvider, responseFormat, responseTransform,
                    completion, priority, useCaches);
        }
    }

    // [endregion]

    // [region] public authentication methods

    /**
     * Attempt to create a new account.
     *
     * @param email
     * @param firstName
     * @param lastName
     * @param password
     * @param gender
     * @param dateOfBirth
     * @param heightInMeters
     * @param weightInKg
     * @param completion
     *            indicates request success or error message.
     */
    public void signUpWithEmail(final String email, final String firstName,
                                final String lastName, final String password, String gender,
                                final Date dateOfBirth, final Double heightInMeters,
                                final Double weightInKg,
                                final ServiceClientCompletion<ResponseResult> completion)
    {
        // Validate email format.
        if (!isEmailFormat(email) && completion != null)
        {
            final ResponseResult result =
                    new ResponseResult(
                            false,
                            "Please enter a valid email.",
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS);
            completion
                    .onCompletion(
                            ServiceResultStatus.FAILED,
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS,
                            result);
            return;
        }

        // create request headers
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/json");

        // body
        final Map<String, Object> body = new HashMap<String, Object>(9);
        body.put(FIRST_NAME, firstName);
        body.put(LAST_NAME, lastName);
        body.put(EMAIL, email);
        body.put(PASSWORD, password);
        body.put(REMEMBER_ME, "true");
        if (!StringHelper.isNullOrEmpty(gender))
        {
            // MALE and FEMALE are only valid options, and case sensitive.
            gender = gender.toUpperCase(Locale.US);
            body.put(GENDER, gender);
        }
        if (dateOfBirth != null)
        {
            // Convert to ISO 8601 string.
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            body.put(DATE_OF_BIRTH, df.format(dateOfBirth) + "T12:00:00Z");
        }
        if (heightInMeters != null)
        {
            body.put(HEIGHT, heightInMeters);
        }
        if (weightInKg != null)
        {
            body.put(WEIGHT, weightInKg);
        }

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(REGISTER_ENDPOINT, ServiceMethod.PUT, headers, null,
                bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!StringHelper.isNullOrEmpty(result.errorMessage)
                                && result.errorMessage.toLowerCase(Locale.US).contains(
                                "attempt to insert duplicate record"))
                        {
                            result.success = false;
                            result.errorMessage =
                                    "That email is already registered.";
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS;
                        }
                        else if (result.success)
                        {
                            final boolean success =
                                    setSharecareToken(json, false, false);
                            if (!success)
                            {
                                result.success = false;
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
                            }
                        }
                        else
                        {
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
                            if (!StringHelper.isNullOrEmpty(result.errorMessage)
                                    && result.errorMessage.toLowerCase(Locale.US)
                                    .contains("invalid email address"))
                            {
                                result.errorMessage = "Please enter a valid email.";
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS;
                            }
                            else if (!StringHelper
                                    .isNullOrEmpty(result.errorMessage)
                                    && result.errorMessage.toLowerCase(Locale.US)
                                    .contains("account already exists"))
                            {
                                result.errorMessage =
                                        "That email is already registered. Please try again with another email.";
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS;
                            }
                        }

                        if (result.responseCode != ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SUCCESS
                                && result.responseCode != ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS)
                        {
                            result.success = false;
                            result.errorMessage = "ERROR";
                        }

                        LogError("signUpWithEmail", result);
                        return result;
                    }
                }, new ServiceClientCompletion<ResponseResult>()
                {

                    @Override
                    public void onCompletion(
                            final ServiceResultStatus serviceResultStatus,
                            final int responseCode, ResponseResult resultValue)
                    {
                        if (completion != null)
                        {
                            if (resultValue == null)
                            {
                                resultValue =
                                        new ResponseResult(
                                                false,
                                                "ERROR",
                                                ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS);
                            }

                            completion.onCompletion(resultValue.success
                                            ? ServiceResultStatus.SUCCESS
                                            : ServiceResultStatus.FAILED,
                                    resultValue.responseCode, resultValue);
                        }
                    }
                });
    }

    /**
     * Attempt to login.
     *
     * @param email
     * @param password
     * @completion indicates request success or error message.
     */
    public void loginWithEmail(final String email, final String password,
                               final ServiceClientCompletion<ResponseResult> completion)
    {
        // Validate email format.
        if (!isEmailFormat(email) && completion != null)
        {
            final ResponseResult result =
                    new ResponseResult(
                            false,
                            "Please enter a valid email.",
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS);
            completion
                    .onCompletion(
                            ServiceResultStatus.FAILED,
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS,
                            result);
            return;
        }

        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", "SSO rDjKqrmf_M7pZGZq6l6V8rPdNHz0h6i_X3fzsRE8AxnqxiYkrwjwFkeotq3pm5YHdiI0wZCQiwh4-zNY-nxmqYgB06GA6ntiS-oKwS3w_cV7FuCPABQ7J_GwclJmz9ogDtcZrLBcWxp31aK4-c86KRhmQ9OgzOW0");
        headers.put("Content-Type", "application/json");

        // Create request parameters.
        final HashMap<String, String> parameters =
                new HashMap<String, String>(1);
        parameters.put("grant_type", PASSWORD);

        // Create request body.
        final HashMap<String, Object> body = new HashMap<String, Object>(3);
        body.put(USERNAME, email);
        body.put(PASSWORD, password);
        body.put(REMEMBER_ME, "true");

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);
        this.beginRequest("http://192.168.201.53:8080/data/user/test-user-1/wall/mobile/private", ServiceMethod.GET, headers,
                parameters, bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final boolean success =
                                    setSharecareToken(json, false, false);
                            if (!success)
                            {
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
                            }
                        }

                        switch (result.responseCode)
                        {
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA:
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR:
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_NONE:
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN:
                                result.success = false;
                                result.errorMessage =
                                        "We're sorry. Something went wrong. Please try again.";
                                break;
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNAUTHORIZED:
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS:
                                result.success = false;
                                result.errorMessage =
                                        "The email address or password you entered did not match our records.";
                                break;
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SUCCESS:
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_CANCELLED:
                            case ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_ALERT_MESSAGE:
                                result.errorMessage = "";
                                break;
                        }

                        LogError("loginWithEmail", result);
                        return result;
                    }
                }, new ServiceClientCompletion<ResponseResult>()
                {
                    @Override
                    public void onCompletion(
                            final ServiceResultStatus serviceResultStatus,
                            final int responseCode, ResponseResult resultValue)
                    {
                        if (completion != null)
                        {
                            if (responseCode == ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNAUTHORIZED
                                    || responseCode == ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_NOT_FOUND)
                            {
                                // Handle 401 error.
                                resultValue =
                                        new ResponseResult(
                                                false,
                                                "The email address or password you entered did not match our records.",
                                                responseCode);
                            }
                            completion.onCompletion(serviceResultStatus,
                                    responseCode, resultValue);
                        }
                    }
                });
    }

    /**
     * Attempt to logout.
     */
    public void logout()
    {
        mSharecareToken = null;
        // Clear the sharecareToken from the settings manager
        SettingsManager.instance.sharecareToken = null;
        SettingsManager.instance.reset();
    }

    /**
     * Check if there is a need to refresh the accessToken.
     *
     * @param completion
     *            the new access token is saved in the ResponseResult
     *            parameters, under the "newAccessToken" key.
     */
    public void checkToRefreshToken(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final long timeDiff =
                mSharecareToken.expiresIn.getTime() - new Date().getTime();
        final long timeDiffInMin = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
        if (timeDiffInMin < TIME_LIMIT_IN_MINUTES)
        {
            refreshToken(completion);
        }
        else if (completion != null)
        {
            completion.onCompletion(ServiceResultStatus.CANCELLED, 0, null);
        }
    }

    /**
     * Attempt to refresh the accessToken.
     *
     * @param completion
     *            the new access token is saved in the ResponseResult
     *            parameters, under the "newAccessToken" key.
     */
    public void refreshToken(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        // Create request parameters.
        final HashMap<String, String> parameters =
                new HashMap<String, String>(2);
        parameters.put("grant_type", "refresh_token");
        parameters.put("refresh_token", mSharecareToken.refreshToken);

        this.beginRequest(REFRESH_TOKEN_ENDPOINT, ServiceMethod.GET, headers,
                parameters, (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final boolean success =
                                    setSharecareToken(json,
                                            mSharecareToken.askMDProfileCreated,
                                            mSharecareToken.preProfileCreation);
                            if (success)
                            {
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put("newAccessToken",
                                        mSharecareToken.accessToken);
                                result.parameters = parameters;
                            }
                        }

                        LogError("refreshToken", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] public account password methods

    /**
     * Attempt to reset the password for the given email.
     *
     * @param email
     * @param completion
     *            indicates request success or error message.
     */
    public void forgotPasswordForEmail(final String email,
                                       final ServiceClientCompletion<ResponseResult> completion)
    {
        // Validate email format.
        if (!isEmailFormat(email) && completion != null)
        {
            final ResponseResult result =
                    new ResponseResult(false, "Please enter a valid email.",
                            HttpStatus.SC_BAD_REQUEST);
            completion.onCompletion(ServiceResultStatus.FAILED,
                    HttpStatus.SC_BAD_REQUEST, result);
            return;
        }

        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        // Create endpoint.
        final String endPoint = String.format(FORGOT_PASSWORD_ENDPOINT, email);

        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        LogError("forgotPasswordForEmail", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to change the password.
     *
     * @param oldPassword
     * @param newPassword
     * @param completion
     *            indicates request success or error message.
     */
    public void changePassword(final String oldPassword,
                               final String newPassword,
                               final ServiceClientCompletion<ResponseResult> completion)
    {
        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/json");

        // Create request parameters.
        final HashMap<String, String> parameters =
                new HashMap<String, String>(2);
        parameters.put("grant_type", "bearer");
        parameters.put("access_token", mSharecareToken.accessToken);

        // Create request body.
        final HashMap<String, String> body = new HashMap<String, String>(2);
        body.put(OLD_PASSWORD, oldPassword);
        body.put(NEW_PASSWORD, newPassword);

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        // Create endpoint.
        final String endPoint =
                String.format(CHANGE_PASSWORD_ENDPOINT, mSharecareToken.accountID);

        this.beginRequest(endPoint, ServiceMethod.POST, headers, parameters,
                bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        LogError("changePassword", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] public profile methods

    // TODO: Currently breaks (404 error) if the profile hasn't been created
    // yet. Need to clarify use case
    // to see if this might happen in production.
    /**
     * Attempt to create, retrieve, or update the user's AskMD profile.
     *
     * @param completion
     *            indicates request success or error messages.
     */
    public void verifyAskMDProfileAndGetProfileWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ASKMD_PROFILE_ENDPOINT, mSharecareToken.accountID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success)
                        {
                            if (!checkResultFromDataService(json))
                            {
                                // Create AskMD profile and get Sharecare
                                // profile.
                                createAskMDProfileWithCompletion(completion);
                            }
                            else
                            {
                                // Extract profile info to create profile
                                // object.
                                if (response != null && response.isJsonObject())
                                {
                                    final JsonObject responseObject =
                                            response.getAsJsonObject();
                                    if (getBooleanFromJson(responseObject,
                                            HAS_ACCEPTED_TERMS_AND_CONDITIONS, false))
                                    {
                                        setAskMDProfileCreatedInSharecareToken();
                                        // Retrieve Sharecare profile.
                                        getProfileWithCompletion(completion);
                                    }
                                    else
                                    {
                                        // Update AskMD profile.
                                        updateAskMDProfileWithCompletion(completion);
                                    }
                                }
                                else
                                {
                                    // Create AskMD profile and get Sharecare
                                    // profile.
                                    createAskMDProfileWithCompletion(completion);
                                }
                            }
                        }
                        else
                        {
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                    LogError(
                                            "verifyAskMDProfileAndGetProfileWithCompletion",
                                            result);
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to create a new AskMD profile. If the profile already exists,
     * then its information is updated.
     *
     * @param completion
     *            if the profile already exists, the updated profile data is
     *            stored in the ResponseResult's parameters property, under the
     *            key PROFILE.
     */
    public void createAskMDProfileWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ASKMD_PROFILE_ENDPOINT, mSharecareToken.accountID);

        // Create request body.
        final HashMap<String, String> body = new HashMap<String, String>(1);
        body.put(HAS_ACCEPTED_TERMS_AND_CONDITIONS, "true");

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success)
                        {
                            final JsonObject jsonObject = json.getAsJsonObject();
                            if (!checkResultFromDataService(json))
                            {
                                // Call was a success but there was no data
                                // (profile may have already been created).
                                result.success = false;
                                final int errorCode =
                                        getIntFromJson(jsonObject, "errorCode", 0);
                                if (errorCode == 1)
                                {
                                    result.responseCode =
                                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
                                    result.errorMessage =
                                            "Profile has already been created.";
                                    getProfileWithCompletion(new ServiceClientCompletion<ResponseResult>()
                                    {
                                        @Override
                                        public void onCompletion(
                                                final ServiceResultStatus serviceResultStatus,
                                                final int responseCode,
                                                final ResponseResult resultValue)
                                        {
                                            if (resultValue.success)
                                            {
                                                setAskMDProfileCreatedInSharecareToken();
                                            }

                                            final Profile profile =
                                                    (Profile)resultValue.parameters
                                                            .get(PROFILE);

                                            // Get medical info.
                                            getMedicalInfoWithCompletion(new ServiceClientCompletion<ResponseResult>()
                                            {
                                                @Override
                                                public void onCompletion(
                                                        final ServiceResultStatus serviceResultStatus,
                                                        final int responseCode,
                                                        final ResponseResult resultValue)
                                                {
                                                    if (profile != null)
                                                    {
                                                        profile.physician =
                                                                (ProfilePhysician)resultValue.parameters
                                                                        .get(PHYSICIAN);
                                                        profile.vitalStats =
                                                                (VitalStats)resultValue.parameters
                                                                        .get("vitalStats");
                                                        profile.insurancePlan =
                                                                (InsuranceProfile)resultValue.parameters
                                                                        .get(INSURANCE);
                                                        final HashMap<String, Object> parameters =
                                                                new HashMap<String, Object>(
                                                                        1);
                                                        parameters.put(PROFILE,
                                                                profile);
                                                        resultValue.parameters =
                                                                parameters;
                                                    }
                                                    if (completion != null)
                                                    {
                                                        completion.onCompletion(
                                                                serviceResultStatus,
                                                                responseCode,
                                                                resultValue);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                                else
                                {
                                    result.responseCode =
                                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN;
                                }
                            }
                            else
                            {
                                if (response != null && response.isJsonObject())
                                {
                                    final JsonObject responseObject =
                                            response.getAsJsonObject();
                                    if (getBooleanFromJson(responseObject,
                                            HAS_ACCEPTED_TERMS_AND_CONDITIONS, false))
                                    {
                                        setAskMDProfileCreatedInSharecareToken();
                                        // Retrieve Sharecare profile.
                                        getProfileWithCompletion(completion);
                                    }
                                    else
                                    {
                                        // Update AskMD's profile.
                                        updateAskMDProfileWithCompletion(completion);
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                    LogError("createAskMDProfileWithCompletion",
                                            result);
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to retrieve the user's profile values.
     *
     * @param completion
     *            if successful, the profile values are stored in the
     *            ResponseResult's parameters property, under the key "profile".
     */
    public void getProfileWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/json");

        // Create request parameters.
        final HashMap<String, String> parameters =
                new HashMap<String, String>(2);
        parameters.put("grant_type", "bearer");
        parameters.put("access_token", mSharecareToken.accessToken);

        // Create endpoint.
        final String endPoint =
                String.format(PROFILE_ENDPOINT, mSharecareToken.accountID);

        this.beginRequest(endPoint, ServiceMethod.GET, headers, parameters,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            // Extract profile info to create profile object.
                            final JsonObject jsonObject = json.getAsJsonObject();
                            final String firstName =
                                    getStringFromJson(jsonObject, FIRST_NAME);
                            final String lastName =
                                    getStringFromJson(jsonObject, LAST_NAME);
                            final String email =
                                    getStringFromJson(jsonObject, EMAIL);
                            final String gender =
                                    getStringFromJson(jsonObject, GENDER);
                            final Date dateOfBirth =
                                    getDateFromJson(jsonObject, DATE_OF_BIRTH);
                            final double height =
                                    getDoubleFromJson(jsonObject, HEIGHT, 0);
                            final double weight =
                                    getDoubleFromJson(jsonObject, WEIGHT, 0);

                            if (firstName != null && lastName != null
                                    && email != null)
                            {
                                final Profile profile = new Profile();
                                profile.firstName = firstName;
                                profile.lastName = lastName;
                                profile.email = email;
                                profile.heightInMeters = height;
                                profile.weightInKg = weight;
                                profile.gender = gender;
                                profile.dateOfBirth = dateOfBirth;
                                updateAskMDProfileWithCompletion(null);
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put(PROFILE, profile);
                                result.parameters = parameters;
                            }
                            else
                            {
                                result.success = false;
                                result.errorMessage = "Bad profile data.";
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_DATA;
                            }
                        }

                        LogError("getProfileWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to update the user's medical information with the values
     * associated with a given profile.
     *
     * @param profile
     * @param completion
     *            indicates request success or error messages.
     */
    public void updateMedicalInfo(final Profile profile,
                                  final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(MEDICAL_INFO_ENDPOINT, mSharecareToken.accountID);

        // Create request body.
        final HashMap<String, HashMap<String, Object>> body =
                new HashMap<String, HashMap<String, Object>>(3);

        HashMap<String, Object> doctor = null;
        if (profile.physician != null)
        {
            doctor = new HashMap<String, Object>(8);
            doctor.put(NAME, profile.physician.name);
            doctor.put(SPECIALTY, profile.physician.specialty);
            doctor.put(ADDRESS, profile.physician.address);
            doctor.put(CITY, profile.physician.city);
            doctor.put(STATE, profile.physician.state);
            doctor.put(ZIP, profile.physician.zip);
            doctor.put(PHONE, profile.physician.phone);
            doctor.put(EMAIL, profile.physician.email);
        }
        body.put(PHYSICIAN, doctor);

        final HashMap<String, Object> insurancePlan =
                new HashMap<String, Object>(3);
        if (profile.insurancePlan != null)
        {
            insurancePlan.put(MEMBER_ID, profile.insurancePlan.identification);
            insurancePlan.put(GROUP_ID, profile.insurancePlan.group);
            if (profile.insurancePlan.insurancePlanAndCarrier == null
                    || StringHelper
                    .isNullOrEmpty(profile.insurancePlan.insurancePlanAndCarrier.planName))
            {
                insurancePlan.put(PLAN, null);
            }
            else
            {
                final HashMap<String, String> insurancePlanAndCarrier =
                        new HashMap<String, String>(4);
                insurancePlanAndCarrier.put(PLAN_ID,
                        profile.insurancePlan.insurancePlanAndCarrier.planId);
                insurancePlanAndCarrier.put(PLAN_NAME,
                        profile.insurancePlan.insurancePlanAndCarrier.planName);
                insurancePlanAndCarrier.put(CARRIER_ID,
                        profile.insurancePlan.insurancePlanAndCarrier.carrierId);
                insurancePlanAndCarrier.put(CARRIER_NAME,
                        profile.insurancePlan.insurancePlanAndCarrier.carrierName);
                insurancePlan.put(PLAN, insurancePlanAndCarrier);
            }
        }
        body.put(INSURANCE, insurancePlan);

        HashMap<String, Object> vitalStats = null;
        if (profile.vitalStats != null)
        {
            vitalStats = new HashMap<String, Object>(5);
            HashMap<String, String> cholesterol = null;
            if (profile.vitalStats.cholesterol != null)
            {
                cholesterol = new HashMap<String, String>(3);
                cholesterol.put(TOTAL,
                        String.valueOf(profile.vitalStats.cholesterol.total));
                cholesterol.put(LDL,
                        String.valueOf(profile.vitalStats.cholesterol.ldl));
                cholesterol.put(HDL,
                        String.valueOf(profile.vitalStats.cholesterol.hdl));
            }
            vitalStats.put(CHOLESTEROL, cholesterol);

            HashMap<String, String> bloodPressure = null;
            if (profile.vitalStats.bloodPressure != null)
            {
                bloodPressure = new HashMap<String, String>(2);
                bloodPressure.put(SYSTOLIC,
                        String.valueOf(profile.vitalStats.bloodPressure.systolic));
                bloodPressure.put(DIASTOLIC,
                        String.valueOf(profile.vitalStats.bloodPressure.diastolic));
            }
            vitalStats.put(BLOOD_PRESSURE, bloodPressure);

            final ArrayList<HashMap<String, String>> medicalConditions =
                    new ArrayList<HashMap<String, String>>();
            if (profile.vitalStats.medicalConditions != null)
            {
                for (final MedicalCondition mc : profile.vitalStats.medicalConditions)
                {
                    final HashMap<String, String> mcJson =
                            new HashMap<String, String>(2);
                    mcJson.put(ID, mc.identifier);
                    mcJson.put(NAME, mc.fulltitle);
                    medicalConditions.add(mcJson);
                }
            }
            vitalStats.put(MEDICAL_CONDITIONS, medicalConditions);

            vitalStats.put(TRIGLYCERIDES,
                    String.valueOf(profile.vitalStats.triglycerides));

            String lastVisitType = DONT_GO;
            if (profile.vitalStats.getLastVisitType() != null)
            {
                switch (profile.vitalStats.getLastVisitType())
                {
                    case ThisMonth:
                        lastVisitType = THIS_MONTH;
                        break;
                    case WithinOneYear:
                        lastVisitType = WITHIN_ONE_YEAR;
                        break;
                    case OneTwoYears:
                        lastVisitType = ONE_TWO_YEARS;
                        break;
                    case ThreeFiveYears:
                        lastVisitType = THREE_FIVE_YEARS;
                        break;
                    case FivePlusYears:
                        lastVisitType = FIVE_PLUS_YEARS;
                        break;
                    case DontGo:
                        lastVisitType = DONT_GO;
                        break;
                }
            }
            vitalStats.put(TIME_SINCE_LAST_VISIT, lastVisitType);

            body.put(VITAL_STATS, vitalStats);
        }

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.POST, headers, null,
                bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        LogError("updateMedicalInfo", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to retrieve the user's medical information.
     *
     * @param completion
     *            if successful, the medical information is stored in the
     *            ResponseResult's parameters property, under the keys
     *            "insurance", "physician", and "vitalStatistics".
     */
    public void getMedicalInfoWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(MEDICAL_INFO_ENDPOINT, mSharecareToken.accountID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success && response != null
                                && response.isJsonObject())
                        {
                            final JsonObject responseJson =
                                    response.getAsJsonObject();
                            ProfilePhysician physician = null;
                            final JsonObject physicianJson =
                                    getJsonObjectFromJson(responseJson, PHYSICIAN);
                            if (physicianJson != null)
                            {
                                final String name =
                                        getStringFromJson(physicianJson, NAME);
                                final String specialty =
                                        getStringFromJson(physicianJson, SPECIALTY);
                                final String address =
                                        getStringFromJson(physicianJson, ADDRESS);
                                final String city =
                                        getStringFromJson(physicianJson, CITY);
                                final String state =
                                        getStringFromJson(physicianJson, STATE);
                                final String zip =
                                        getStringFromJson(physicianJson, ZIP);
                                final String phone =
                                        getStringFromJson(physicianJson, PHONE);
                                final String email =
                                        getStringFromJson(physicianJson, EMAIL);
                                physician =
                                        new ProfilePhysician(name, specialty, address,
                                                city, state, zip, phone, email);
                            }

                            InsuranceProfile insurance = null;
                            InsurancePlanAndCarrier planAndCarrier = null;
                            final JsonObject insuranceJson =
                                    getJsonObjectFromJson(responseJson, INSURANCE);
                            if (insuranceJson != null)
                            {
                                final JsonObject planAndCarrierJson =
                                        getJsonObjectFromJson(insuranceJson, PLAN);
                                if (planAndCarrierJson != null)
                                {
                                    final String planId =
                                            getStringFromJson(planAndCarrierJson,
                                                    PLAN_ID);
                                    final String planName =
                                            getStringFromJson(planAndCarrierJson,
                                                    PLAN_NAME);
                                    final String carrierId =
                                            getStringFromJson(planAndCarrierJson,
                                                    CARRIER_ID);
                                    final String carrierName =
                                            getStringFromJson(planAndCarrierJson,
                                                    CARRIER_NAME);
                                    planAndCarrier =
                                            new InsurancePlanAndCarrier(planId,
                                                    planName, carrierId, carrierName);
                                }
                                final String identification =
                                        getStringFromJson(insuranceJson, MEMBER_ID);
                                final String group =
                                        getStringFromJson(insuranceJson, GROUP_ID);
                                insurance =
                                        new InsuranceProfile(planAndCarrier,
                                                identification, group);
                            }

                            VitalStats vitalStats = null;
                            final JsonObject vitalStatsJson =
                                    getJsonObjectFromJson(responseJson, VITAL_STATS);
                            if (vitalStatsJson != null)
                            {
                                Cholesterol cholesterol = null;
                                final JsonObject cholesterolJson =
                                        getJsonObjectFromJson(vitalStatsJson,
                                                CHOLESTEROL);
                                if (cholesterolJson != null)
                                {
                                    final int total =
                                            getIntFromJson(cholesterolJson, TOTAL, 0);
                                    final int ldl =
                                            getIntFromJson(cholesterolJson, LDL, 0);
                                    final int hdl =
                                            getIntFromJson(cholesterolJson, HDL, 0);
                                    cholesterol = new Cholesterol(total, ldl, hdl);
                                }

                                final int triglycerides =
                                        getIntFromJson(vitalStatsJson, TRIGLYCERIDES, 0);

                                BloodPressure bloodPressure = null;
                                final JsonObject bloodPressureJson =
                                        getJsonObjectFromJson(vitalStatsJson,
                                                BLOOD_PRESSURE);
                                if (bloodPressureJson != null)
                                {
                                    final int systolic =
                                            getIntFromJson(bloodPressureJson, SYSTOLIC,
                                                    0);
                                    final int diastolic =
                                            getIntFromJson(bloodPressureJson,
                                                    DIASTOLIC, 0);
                                    bloodPressure =
                                            new BloodPressure(systolic, diastolic);
                                }

                                final ArrayList<MedicalCondition> medicalConditions =
                                        new ArrayList<MedicalCondition>();
                                if (getJsonArrayFromJson(vitalStatsJson,
                                        MEDICAL_CONDITIONS) != null)
                                {
                                    for (final JsonElement element : getJsonArrayFromJson(
                                            vitalStatsJson, MEDICAL_CONDITIONS))
                                    {
                                        if (element.isJsonObject())
                                        {
                                            final JsonObject conditionJson =
                                                    element.getAsJsonObject();
                                            final String identifier =
                                                    getStringFromJson(conditionJson, ID);
                                            final String name =
                                                    getStringFromJson(conditionJson,
                                                            NAME);
                                            final MedicalCondition condition =
                                                    new MedicalCondition(identifier,
                                                            name);
                                            medicalConditions.add(condition);
                                        }
                                    }
                                }

                                TimeSinceLastVisitType lastVisitType =
                                        TimeSinceLastVisitType.DontGo;
                                final String lastVisitString =
                                        getStringFromJson(vitalStatsJson,
                                                TIME_SINCE_LAST_VISIT);
                                if (lastVisitString != null)
                                {
                                    if (lastVisitString.equals(THIS_MONTH))
                                    {
                                        lastVisitType =
                                                TimeSinceLastVisitType.ThisMonth;
                                    }
                                    else if (lastVisitString
                                            .equals(WITHIN_ONE_YEAR))
                                    {
                                        lastVisitType =
                                                TimeSinceLastVisitType.WithinOneYear;
                                    }
                                    else if (lastVisitString.equals(ONE_TWO_YEARS))
                                    {
                                        lastVisitType =
                                                TimeSinceLastVisitType.OneTwoYears;
                                    }
                                    else if (lastVisitString
                                            .equals(THREE_FIVE_YEARS))
                                    {
                                        lastVisitType =
                                                TimeSinceLastVisitType.ThreeFiveYears;
                                    }
                                    else if (lastVisitString
                                            .equals(FIVE_PLUS_YEARS))
                                    {
                                        lastVisitType =
                                                TimeSinceLastVisitType.FivePlusYears;
                                    }
                                }

                                vitalStats =
                                        new VitalStats(cholesterol, triglycerides,
                                                bloodPressure, lastVisitType);
                                vitalStats.medicalConditions =
                                        new ObservableArrayList<MedicalCondition>();
                                for (final MedicalCondition condition : medicalConditions)
                                {
                                    vitalStats.medicalConditions.add(condition);
                                }
                            }
                            final HashMap<String, Object> parameters =
                                    new HashMap<String, Object>(3);
                            parameters.put(INSURANCE, insurance);
                            parameters.put(PHYSICIAN, physician);
                            parameters.put(VITAL_STATS, vitalStats);
                            result.parameters = parameters;
                        }
                        LogError("getMedicalInfoWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to update the user's profile data with values from the given
     * profile.
     *
     * @param profile
     * @param completion
     *            indicates request success or error messages.
     */
    public void updateProfile(final Profile profile,
                              final ServiceClientCompletion<ResponseResult> completion)
    {
        // Validate email format.
        if (!isEmailFormat(profile.email) && completion != null)
        {
            final ResponseResult result =
                    new ResponseResult(
                            false,
                            "Please enter a valid email.",
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS);
            completion
                    .onCompletion(
                            ServiceResultStatus.FAILED,
                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS,
                            result);
            return;
        }

        // Create request headers.
        final HashMap<String, String> headers = new HashMap<String, String>(2);
        headers.put("Authorization", AUTHORIZATION_HEADER);
        headers.put("Content-Type", "application/json");

        // Create request parameters.
        final HashMap<String, String> parameters =
                new HashMap<String, String>(2);
        parameters.put("grant_type", "bearer");
        parameters.put("access_token", mSharecareToken.accessToken);

        // Create request body.
        final HashMap<String, Object> body = new HashMap<String, Object>(8);
        body.put(FIRST_NAME, profile.firstName);
        body.put(LAST_NAME, profile.lastName);
        body.put(EMAIL, profile.email);
        body.put(GENDER, profile.gender);
        if (profile.dateOfBirth != null)
        {
            body.put(DATE_OF_BIRTH, profile.dateOfBirth.getTime());
        }
        else
        {
            body.put(DATE_OF_BIRTH, null);
        }
        body.put(HEIGHT, profile.heightInMeters);
        body.put(WEIGHT, profile.weightInKg);
        if (!StringHelper.isNullOrEmpty(profile.avatarURI))
        {
            final HashMap<String, String> image =
                    new HashMap<String, String>(3);
            image.put(TYPE, IMAGE);
            image.put(URL, profile.avatarURI);
            image.put(DESCRIPTION, PROFILE_AVATAR);
            body.put(IMAGE, image);
        }

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        final String endPoint =
                String.format(UPDATE_PROFILE_ENDPOINT, mSharecareToken.accountID);

        this.beginRequest(endPoint, ServiceMethod.POST, headers, parameters,
                bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!StringHelper.isNullOrEmpty(result.errorMessage))
                        {
                            if (result.errorMessage.toLowerCase(Locale.US)
                                    .contains("invalid email address"))
                            {
                                result.errorMessage = "Please enter a valid email.";
                            }
                            else if (result.errorMessage.toLowerCase(Locale.US)
                                    .contains("account already exists"))
                            {
                                result.errorMessage =
                                        "That email is already registered. Please try again with another email.";
                            }
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_BAD_PARAMETERS;
                        }
                        LogError("updateProfile", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to update the user's profile avatar with the image at the given
     * uri.
     *
     * @param uri
     * @param completion
     *            indicates success or error messages.
     */
    public void updateProfileAvatarWithURI(final String uri,
                                           final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(PROFILE_URI_ENDPOINT, mSharecareToken.accountID);

        // Create request body.
        final HashMap<String, String> image = new HashMap<String, String>(3);
        image.put(TYPE, IMAGE);
        image.put("uri", uri);
        image.put(DESCRIPTION, PROFILE_AVATAR);

        final HashMap<String, HashMap<String, String>> body =
                new HashMap<String, HashMap<String, String>>(1);
        body.put(IMAGE, image);

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        LogError("updateProfileAvatarWithURI", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] Secondary profile methods

    /**
     * Attempt to retrieve the user's secondary profiles.
     *
     * @param completion
     *            if successful, the profiles stored in an ArrayList in the
     *            ResponseResult's parameters property, under the key "profile".
     */
    public void getSecondaryProfilesWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ADD_FAMILY_ENDPOINT, mSharecareToken.accountID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null && response.isJsonArray())
                            {
                                final JsonArray responseArray =
                                        response.getAsJsonArray();
                                final ArrayList<SecondaryProfile> profiles =
                                        new ArrayList<SecondaryProfile>();
                                for (final JsonElement element : responseArray)
                                {
                                    if (element.isJsonObject())
                                    {
                                        final JsonObject profileResponse =
                                                element.getAsJsonObject();
                                        final String profileID =
                                                getStringFromJson(profileResponse, ID);
                                        final String name =
                                                getStringFromJson(profileResponse, NAME);
                                        final String gender =
                                                getStringFromJson(profileResponse,
                                                        GENDER);
                                        final Date dateOfBirth =
                                                getDateFromJson(profileResponse,
                                                        DATE_OF_BIRTH);
                                        final double height =
                                                getDoubleFromJson(profileResponse,
                                                        HEIGHT, 0);
                                        final double weight =
                                                getDoubleFromJson(profileResponse,
                                                        WEIGHT, 0);

                                        final JsonObject imageJson =
                                                getJsonObjectFromJson(profileResponse,
                                                        IMAGE);
                                        String url = null;
                                        if (imageJson != null)
                                        {
                                            final JsonObject locations =
                                                    getJsonObjectFromJson(imageJson,
                                                            LOCATIONS);
                                            if (locations != null
                                                    && getStringFromJson(
                                                    locations,
                                                    getAssetSizeString(AssetSize.Size60x60)) != null)
                                            {
                                                url =
                                                        "https:"
                                                                + getStringFromJson(
                                                                locations,
                                                                getAssetSizeString(AssetSize.Size60x60));
                                            }
                                        }

                                        ProfilePhysician physician = null;
                                        final JsonObject physicianJson =
                                                getJsonObjectFromJson(profileResponse,
                                                        PHYSICIAN);
                                        if (physicianJson != null)
                                        {
                                            final String physicianName =
                                                    getStringFromJson(physicianJson,
                                                            NAME);
                                            final String specialty =
                                                    getStringFromJson(physicianJson,
                                                            SPECIALTY);
                                            final String address =
                                                    getStringFromJson(physicianJson,
                                                            ADDRESS);
                                            final String city =
                                                    getStringFromJson(physicianJson,
                                                            CITY);
                                            final String state =
                                                    getStringFromJson(physicianJson,
                                                            STATE);
                                            final String zip =
                                                    getStringFromJson(physicianJson,
                                                            ZIP);
                                            final String phone =
                                                    getStringFromJson(physicianJson,
                                                            PHONE);
                                            final String email =
                                                    getStringFromJson(physicianJson,
                                                            EMAIL);
                                            physician =
                                                    new ProfilePhysician(physicianName,
                                                            specialty, address, city,
                                                            state, zip, phone, email);
                                        }

                                        InsuranceProfile insurance = null;
                                        final JsonObject insuranceJson =
                                                getJsonObjectFromJson(profileResponse,
                                                        INSURANCE);
                                        if (insuranceJson != null)
                                        {
                                            final String identification =
                                                    getStringFromJson(insuranceJson,
                                                            MEMBER_ID);
                                            final String group =
                                                    getStringFromJson(insuranceJson,
                                                            GROUP_ID);
                                            InsurancePlanAndCarrier insurancePlanAndCarrier =
                                                    null;
                                            final JsonObject planJson =
                                                    getJsonObjectFromJson(
                                                            insuranceJson, PLAN);
                                            if (planJson != null)
                                            {
                                                final String planId =
                                                        getStringFromJson(planJson,
                                                                PLAN_ID);
                                                final String planName =
                                                        getStringFromJson(planJson,
                                                                PLAN_NAME);
                                                final String carrierId =
                                                        getStringFromJson(planJson,
                                                                CARRIER_ID);
                                                final String carrierName =
                                                        getStringFromJson(planJson,
                                                                CARRIER_NAME);
                                                insurancePlanAndCarrier =
                                                        new InsurancePlanAndCarrier(
                                                                planId, planName,
                                                                carrierId, carrierName);
                                            }
                                            insurance =
                                                    new InsuranceProfile(
                                                            insurancePlanAndCarrier,
                                                            identification, group);
                                        }

                                        if (!StringHelper.isNullOrEmpty(profileID)
                                                && !StringHelper.isNullOrEmpty(name))
                                        {
                                            final SecondaryProfile profile =
                                                    new SecondaryProfile(name, height,
                                                            weight, gender, dateOfBirth);
                                            profile.identifier = profileID;
                                            profile.physician = physician;
                                            profile.insurancePlan = insurance;
                                            profile.avatarURI = url;
                                            if (url != null)
                                            {
                                                try
                                                {
                                                    final URL avatarUrl =
                                                            new URL(url);
                                                    profile.avatar =
                                                            BitmapFactory
                                                                    .decodeStream(avatarUrl
                                                                            .openConnection()
                                                                            .getInputStream());
                                                }
                                                catch (final MalformedURLException me)
                                                {
//                                                    Crashlytics.logException(me);
                                                }
                                                catch (final IOException ie)
                                                {
//                                                    Crashlytics.logException(ie);
                                                }
                                            }
                                            profiles.add(profile);
                                        }
                                    }
                                }
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put(PROFILE, profiles);
                                result.parameters = parameters;
                            }
                        }
                        LogError("getSecondaryProfilesWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to insert a new secondary profile.
     *
     * @param profile
     * @param completion
     *            indicates request success or error messages.
     */
    public void addSecondaryProfile(final SecondaryProfile profile,
                                    final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);

        // Create request body.
        final HashMap<String, Object> body = new HashMap<String, Object>(6);
        body.put(NAME, profile.getName());
        body.put(GENDER, profile.gender);
        if (!StringHelper.isNullOrEmpty(profile.identifier))
        {
            body.put(ID, profile.identifier);
        }
        body.put(HEIGHT, profile.heightInMeters);
        body.put(WEIGHT, profile.weightInKg);
        if (!StringHelper.isNullOrEmpty(profile.avatarURI))
        {
            final HashMap<String, String> image =
                    new HashMap<String, String>(3);
            image.put(TYPE, IMAGE);
            image.put(URL, profile.avatarURI);
            image.put(DESCRIPTION, PROFILE_AVATAR);
            body.put(IMAGE, image);
        }

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        // Create endpoint.
        final String endPoint =
                String.format(ADD_FAMILY_ENDPOINT, mSharecareToken.accountID);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null)
                            {
                                try
                                {
                                    final String idString = response.getAsString();
                                    final HashMap<String, Object> parameters =
                                            new HashMap<String, Object>(1);
                                    parameters.put(ID, idString);
                                    result.parameters = parameters;
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        else
                        {
                            result.errorMessage =
                                    "We're sorry. Something went wrong adding a family member. Please try again.";
                        }
                        LogError("addSecondaryProfile", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to update a secondary profile.
     *
     * @param profile
     * @param completion
     *            indicates request success or error messages.
     */
    public void updateSecondaryProfile(final SecondaryProfile profile,
                                       final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);

        // Create request body.
        final HashMap<String, Object> body = new HashMap<String, Object>(9);
        body.put(ID, profile.identifier);
        body.put(NAME, profile.getName());
        body.put(GENDER, profile.gender);
        if (profile.dateOfBirth != null)
        {
            body.put(DATE_OF_BIRTH, profile.dateOfBirth.getTime());
        }
        body.put(HEIGHT, profile.heightInMeters);
        body.put(WEIGHT, profile.weightInKg);
        if (!StringHelper.isNullOrEmpty(profile.avatarURI))
        {
            final HashMap<String, String> image =
                    new HashMap<String, String>(3);
            image.put(TYPE, IMAGE);
            image.put(URL, profile.avatarURI);
            image.put(DESCRIPTION, PROFILE_AVATAR);
            body.put(IMAGE, image);
        }

        HashMap<String, String> physicianJson = null;
        if (profile.physician != null)
        {
            physicianJson = new HashMap<String, String>(8);
            physicianJson.put(NAME, profile.physician.name);
            physicianJson.put(SPECIALTY, profile.physician.specialty);
            physicianJson.put(ADDRESS, profile.physician.address);
            physicianJson.put(CITY, profile.physician.city);
            physicianJson.put(STATE, profile.physician.state);
            physicianJson.put(ZIP, profile.physician.zip);
            physicianJson.put(PHONE, profile.physician.phone);
            physicianJson.put(EMAIL, profile.physician.email);
        }
        body.put(PHYSICIAN, physicianJson);

        HashMap<String, Object> insuranceJson = null;
        if (profile.insurancePlan != null)
        {
            insuranceJson = new HashMap<String, Object>(3);
            insuranceJson.put(MEMBER_ID, profile.insurancePlan.identification);
            insuranceJson.put(GROUP_ID, profile.insurancePlan.group);
            HashMap<String, String> planJson = null;
            if (profile.insurancePlan.insurancePlanAndCarrier != null
                    && !StringHelper
                    .isNullOrWhitespace(profile.insurancePlan.insurancePlanAndCarrier.planId))
            {
                planJson = new HashMap<String, String>(4);
                planJson.put(PLAN_ID,
                        profile.insurancePlan.insurancePlanAndCarrier.planId);
                planJson.put(PLAN_NAME,
                        profile.insurancePlan.insurancePlanAndCarrier.planName);
                planJson.put(CARRIER_ID,
                        profile.insurancePlan.insurancePlanAndCarrier.carrierId);
                planJson.put(CARRIER_NAME,
                        profile.insurancePlan.insurancePlanAndCarrier.carrierName);
            }
            insuranceJson.put(PLAN, planJson);
        }
        body.put(INSURANCE, insuranceJson);

        final Gson gson = new GsonBuilder().serializeNulls().create();
        final String bodyJson = gson.toJson(body);

        // Create endpoint.
        final String endPoint =
                String.format(EDIT_FAMILY_ENDPOINT, mSharecareToken.accountID,
                        profile.identifier);

        this.beginRequest(endPoint, ServiceMethod.POST, headers, null,
                bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!result.success)
                        {
                            result.errorMessage =
                                    "We're sorry. Something went wrong adding a family member. Please try again.";
                        }
                        LogError("updateSecondaryProfile", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to delete a secondary profile.
     *
     * @param profile
     * @param completion
     *            indicates request success or error messages.
     */
    public void deleteSecondaryProfile(final SecondaryProfile profile,
                                       final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(DELETE_FAMILY_ENDPOINT, mSharecareToken.accountID,
                        profile.identifier);
        this.beginRequest(endPoint, ServiceMethod.DELETE, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!result.success)
                        {
                            result.errorMessage =
                                    "We're sorry. Something went wrong deleting a family member. Please try again.";
                        }
                        LogError("deleteSecondaryProfile", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to update the avatar of a secondary profile.
     *
     * @param profile
     * @param uri
     * @param completion
     *            indicates request success or error messages.
     */
    public void updateSecondaryProfileAvatar(final SecondaryProfile profile,
                                             final String uri,
                                             final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(EDIT_FAMILY_ENDPOINT, mSharecareToken.accountID,
                        profile.identifier);

        // Create request body.
        final HashMap<String, String> image = new HashMap<String, String>(3);
        image.put(TYPE, IMAGE);
        image.put(URL, uri);
        image.put(DESCRIPTION, PROFILE_AVATAR);
        final HashMap<String, Object> body = new HashMap<String, Object>(1);
        body.put(IMAGE, image);

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        LogError("updateSecondaryProfileAvatar", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] Consult/session methods

    /**
     * Attempt to retrieve and delete all consultation documents with version
     * older than the minimum.
     *
     * @param completion
     *            indicates request success or error messages.
     */
    public void deleteOldVersionConsults(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ALL_CONSULTS_ENDPOINT, mSharecareToken.accountID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null && response.isJsonArray())
                            {
                                final JsonArray responseArray =
                                        response.getAsJsonArray();
                                for (final JsonElement element : responseArray)
                                {
                                    if (element.isJsonObject())
                                    {
                                        final JsonObject consult =
                                                element.getAsJsonObject();
                                        final String docID =
                                                getStringFromJson(consult, ID);
                                        if (consult.get(METADATA) != null
                                                && consult.get(METADATA).isJsonObject())
                                        {
                                            final JsonObject metadata =
                                                    consult.get(METADATA)
                                                            .getAsJsonObject();
                                            // Check version number.
                                            final String version =
                                                    getStringFromJson(metadata, VERSION);
                                            final String type =
                                                    getStringFromJson(metadata, TYPE);

                                            // Delete if version number is older
                                            // than OLDEST_SUPPORTED_VERSION
                                            // and type is session, userData, or
                                            // poptList.
                                            if (!StringHelper
                                                    .isNullOrEmpty(version)
                                                    && version
                                                    .compareToIgnoreCase(OLDEST_SUPPORTED_VERSION) < 0
                                                    && (type.equals(SESSION)
                                                    || type.equals(FND_LIST)
                                                    || type.equals(USER_DATA) || type
                                                    .equals(POPT_LIST)))
                                            {
                                                deleteConsultWithSessionDocumentID(
                                                        docID, null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        LogError("deleteOldVersionConsults", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to retrieve all of the user's consult summaries.
     *
     * @param completion
     *            if successful, the user's consult summaries are stored in a
     *            sorted ArrayList under the ResponseResult's parameters
     *            property, under the key "consultSummaries".
     */
    public void getConsultsWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_CONSULTS_ENDPOINT, mSharecareToken.accountID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null && response.isJsonArray())
                            {
                                final JsonArray responseArray =
                                        response.getAsJsonArray();
                                final ArrayList<Consultation> consults =
                                        new ArrayList<Consultation>();
                                for (final JsonElement element : responseArray)
                                {
                                    if (element.isJsonObject())
                                    {
                                        final JsonObject consultJson =
                                                element.getAsJsonObject();
                                        final String docID =
                                                getStringFromJson(consultJson, ID);
                                        String title =
                                                getStringFromJson(consultJson, TITLE);
                                        final int lastIndex =
                                                title.lastIndexOf(" Diagnosis");
                                        if (lastIndex > -1)
                                        {
                                            title =
                                                    new StringBuilder(title).replace(
                                                            lastIndex,
                                                            lastIndex + title.length(), "")
                                                            .toString();
                                            // TODO: Figure out what this is
                                            // used for, and see
                                            // if there's a smarter way than
                                            // what's used on iOS.
                                        }

                                        if (consultJson.get(METADATA) != null
                                                && consultJson.get(METADATA)
                                                .isJsonObject())
                                        {
                                            final JsonObject metadata =
                                                    consultJson.get(METADATA)
                                                            .getAsJsonObject();
                                            // Check version number; ignore if
                                            // null or older than
                                            // OLDEST_SUPPORTED_VERSION.
                                            final String version =
                                                    getStringFromJson(metadata, VERSION);
                                            if (StringHelper.isNullOrEmpty(version)
                                                    || version
                                                    .compareToIgnoreCase(OLDEST_SUPPORTED_VERSION) < 0)
                                            {
                                                continue;
                                            }
                                            // Don't process if marked as
                                            // deleted.
                                            final boolean deleted =
                                                    getBooleanFromJson(metadata,
                                                            DELETED, false);
                                            if (deleted)
                                            {
                                                continue;
                                            }

                                            // Don't process if missing document
                                            // IDs.
                                            final String userDocPrivateID =
                                                    getStringFromJson(metadata,
                                                            USER_DATA_PRIVATE_DOC_ID);
                                            final String sessionDocID =
                                                    getStringFromJson(metadata,
                                                            SESSION_DOC_ID);
                                            final String poptListDocID =
                                                    getStringFromJson(metadata,
                                                            POPT_LIST_DOC_ID);
                                            final String fndListDocID =
                                                    getStringFromJson(metadata,
                                                            FND_LIST_DOC_ID);
                                            if (StringHelper
                                                    .isNullOrEmpty(userDocPrivateID)
                                                    || StringHelper
                                                    .isNullOrEmpty(sessionDocID)
                                                    || StringHelper
                                                    .isNullOrEmpty(poptListDocID)
                                                    || StringHelper
                                                    .isNullOrEmpty(fndListDocID))
                                            {
                                                continue;
                                            }

                                            final Date dateAdded =
                                                    getDateFromJson(metadata,
                                                            DATE_ADDED);
                                            final String topicCategory =
                                                    getStringFromJson(metadata,
                                                            CATEGORY);
                                            final String topicID =
                                                    getStringFromJson(metadata,
                                                            TOPIC_ID);
                                            final String infocardID =
                                                    getStringFromJson(metadata,
                                                            INFOCARD_ID);
                                            final String topicRev =
                                                    getStringFromJson(metadata, REV);
                                            final String name =
                                                    getStringFromJson(metadata, NAME);
                                            final String profileID =
                                                    getStringFromJson(metadata,
                                                            PROFILE_ID);
                                            final int numCausesFlagged =
                                                    getIntFromJson(metadata,
                                                            NUM_CAUSES_FLAGGED, 0);
                                            final int numPhysiciansCalled =
                                                    getIntFromJson(metadata,
                                                            NUM_PHYSICIANS_CALLED, 0);
                                            final boolean hasNote =
                                                    getBooleanFromJson(metadata,
                                                            HAS_NOTE, false);
                                            final ConsultSummary consultSummary =
                                                    new ConsultSummary(docID,
                                                            userDocPrivateID, sessionDocID,
                                                            poptListDocID, fndListDocID,
                                                            dateAdded, topicCategory,
                                                            topicID, infocardID, topicRev,
                                                            title, name, numCausesFlagged,
                                                            numPhysiciansCalled, hasNote,
                                                            profileID);
                                            consults.add(new Consultation(
                                                    consultSummary));
                                        }
                                    }
                                }
                                if (consults.size() > 0)
                                {
                                    Collections.sort(consults);
                                    final HashMap<String, Object> parameters =
                                            new HashMap<String, Object>(1);
                                    parameters.put("consults", consults);
                                    result.parameters = parameters;
                                }
                            }
                            else if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                                result.success = false;
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                            }
                        }
                        LogError("getConsultsWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to retrieve the document with the given documentID.
     *
     * @param documentID
     * @param completion
     *            if successful, the document is stored in the ResponseResult's
     *            parameters property, under the key "document".
     */
    public void getDocumentByID(final String documentID,
                                final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_SESSION_DOCUMENT_ENDPOINT,
                        mSharecareToken.accountID, documentID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null && response.isJsonObject())
                            {
                                final JsonObject responseJson =
                                        response.getAsJsonObject();
                                final String sessionDocument =
                                        getStringFromJson(responseJson, DOCUMENT);
                                if (sessionDocument != null)
                                {
                                    final HashMap<String, Object> parameters =
                                            new HashMap<String, Object>(1);
                                    parameters.put(DOCUMENT, sessionDocument);
                                    result.parameters = parameters;
                                }
                            }
                            else if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                                result.success = false;
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                            }
                        }
                        LogError("getDocumentByID", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to add a document to the given consultation.
     *
     * @param consultation
     * @param documentType
     * @param completion
     *            if successful, the new document's ID is stored in the
     *            ResponseResult's parameters property, under the key "id".
     * @throws InvalidDocumentTypeException
     */
    public void addDocumentForConsult(final Consultation consultation,
                                      final ConsultSummary.ConsultSummaryDocumentType documentType,
                                      final ServiceClientCompletion<ResponseResult> completion)
            throws ConsultSummary.InvalidDocumentTypeException
    {
        saveDocumentForConsult(consultation, documentType, false, completion);
    }

    /**
     * Attempt to edit a document to the given consultation.
     *
     * @param consultation
     * @param documentType
     * @param completion
     *            if successful, the edited document's ID is stored in the
     *            ResponseResult's parameters property, under the key "id".
     * @throws InvalidDocumentTypeException
     */
    public void editDocumentForConsult(final Consultation consultation,
                                       final ConsultSummary.ConsultSummaryDocumentType documentType,
                                       final ServiceClientCompletion<ResponseResult> completion)
            throws ConsultSummary.InvalidDocumentTypeException
    {
        saveDocumentForConsult(consultation, documentType, true, completion);
    }

    /**
     * Attempt to delete the consultation with the specified sessionDocumentID.
     *
     * @param sessionDocumentID
     * @param completion
     *            indicates request success or error messages.
     */
    public void deleteConsultWithSessionDocumentID(
            final String sessionDocumentID,
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(DELETE_CONSULT_ENDPOINT, mSharecareToken.accountID,
                        sessionDocumentID);
        this.beginRequest(endPoint, ServiceMethod.DELETE, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        LogError("deleteConsultWithSessionDocumentID", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] share consultation methods
    public void emailConsultWithConsultID(final String consultID,
                                          final String shareToName, final String shareToEmail,
                                          final boolean isProvider,
                                          final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(EMAIL_EXISTS_ENDPOINT, shareToEmail);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonObject jsonObject = json.getAsJsonObject();
                        final String resultFromJson =
                                getStringFromJson(jsonObject, "result");
                        if (!result.success
                                || (resultFromJson != null && resultFromJson
                                .equals("FAILURE")))
                        {
                            result.success = false;
                            if (resultFromJson != null
                                    && resultFromJson.equals("FAILURE"))
                            {
                                result.errorMessage =
                                        getStringFromJson(jsonObject,
                                                SERVICECLIENT_ERROR_MESSAGE_KEY);
                            }
                        }

                        if (!StringHelper.isNullOrEmpty(result.errorMessage))
                        {
                            LogError("emailConsultWithConsultID", result);
                            completion.onCompletion(ServiceResultStatus.FAILED,
                                    result.responseCode, result);
                            return result;
                        }

                        final JsonElement response = getResponseFromJson(json);
                        if (response != null && response.isJsonObject())
                        {
                            final JsonObject responseJson =
                                    response.getAsJsonObject();
                            final String userID =
                                    getStringFromJson(responseJson, "_id");
                            final HashMap<String, Object> parameters =
                                    new HashMap<String, Object>();
                            parameters.put(ID, userID);
                            result.parameters = parameters;
                        }

                        return result;
                    }
                }, completion);
    }

    public void emailConsultToRegisteredEmail(final String consultID,
                                              final String userID,
                                              final ServiceClientCompletion<ResponseResult> completion)
    {
        emailConsult(consultID, userID, null, null, false, completion);
    }

    public void emailConsultToUnregisteredEmail(final String consultID,
                                                final String shareToName, final String shareToEmail,
                                                final boolean isProvider,
                                                final ServiceClientCompletion<ResponseResult> completion)
    {
        emailConsult(consultID, null, shareToName, shareToEmail, isProvider,
                completion);
    }

    public void getPrintableConsultWithConsultID(final String consultID,
                                                 final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(PRINT_CONSULT_ENDPOINT, mSharecareToken.accountID,
                        consultID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (byte[])null, ServiceResponseFormat.RAW,
                new ServiceResponseTransform<byte[], ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(final byte[] data)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result = new ResponseResult();
                        result.success = false;
                        result.errorMessage =
                                "Unknown error downloading printable PDF.";
                        result.responseCode =
                                ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN;
                        if (data.length > 1024)
                        {
                            final String pdfHeader = "%PDF";
                            try
                            {
                                final int index =
                                        KMPHelper.indexOf(data,
                                                pdfHeader.getBytes("UTF-8"));
                                if (index > -1)
                                {
                                    result.success = true;
                                    result.errorMessage = null;
                                    result.responseCode =
                                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SUCCESS;
                                    final HashMap<String, Object> parameters =
                                            new HashMap<String, Object>();
                                    parameters.put("PDF", data);
                                    result.parameters = parameters;
                                }
                                else
                                {
                                    result.errorMessage =
                                            "No %PDF header found in data.";
                                }
                            }
                            catch (final UnsupportedEncodingException e)
                            {
//                                Crashlytics.logException(e);
                            }

                        }
                        else
                        {
                            result.errorMessage = "Data too small to be PDF.";
                        }
                        LogError("getPrintableConsultWithConsultID", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] consult asset methods

    public void getConsultAssets(final String consultID,
                                 final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_ALL_CONSULT_ASSETS_ENDPOINT,
                        mSharecareToken.accountID, consultID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (!checkResultFromDataService(json))
                        {
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN;
                        }
                        else if (!result.success && response != null)
                        {
                            try
                            {
                                result.errorMessage = response.getAsString();
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                        }
                        else if (response != null && response.isJsonArray())
                        {
                            final JsonArray responseArray =
                                    response.getAsJsonArray();
                            final ArrayList<Asset> assets = new ArrayList<Asset>();
                            for (final JsonElement element : responseArray)
                            {
                                if (element.isJsonObject())
                                {
                                    final JsonObject assetJson =
                                            element.getAsJsonObject();
                                    final String type =
                                            getStringFromJson(assetJson, TYPE);
                                    if (!StringHelper.isNullOrEmpty(type))
                                    {
                                        if (type.equals(IMAGE))
                                        {
                                            final JsonObject image =
                                                    getJsonObjectFromJson(assetJson,
                                                            LOCATIONS);
                                            if (image != null)
                                            {
                                                final String imageUri =
                                                        getStringFromJson(assetJson,
                                                                URI);
                                                final String imageUrl =
                                                        "https:"
                                                                + getStringFromJson(
                                                                image,
                                                                getAssetSizeString(AssetSize.Size290x290));
                                                final String thumbnailUrl =
                                                        "https:"
                                                                + getStringFromJson(
                                                                image,
                                                                getAssetSizeString(AssetSize.Size60x60));
                                                final Asset asset =
                                                        Asset.imageAsset(imageUri,
                                                                imageUrl, thumbnailUrl);
                                                assets.add(asset);
                                            }
                                        }
                                        else if (type.equals(VIDEO))
                                        {
                                            final String videoUri =
                                                    getStringFromJson(assetJson, URI);
                                            final String brightcoveID =
                                                    getStringFromJson(assetJson,
                                                            SOURCE_ID);
                                            final String thumbnailUrl =
                                                    getStringFromJson(assetJson,
                                                            GALLERY);
                                            final Asset asset =
                                                    Asset.videoAsset(videoUri,
                                                            brightcoveID, thumbnailUrl);
                                            assets.add(asset);
                                        }
                                    }
                                }
                            }
                            if (assets.size() > 0)
                            {
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put("ASSETS", assets);
                                result.parameters = parameters;
                            }
                        }
                        LogError("getConsultAssets", result);
                        return result;
                    }
                }, completion);
    }

    public void deleteConsultAssetWithConsultID(final String consultID,
                                                final String assetID,
                                                final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(DELETE_CONSULT_ASSET_ENDPOINT,
                        mSharecareToken.accountID, consultID, assetID);
        this.beginRequest(endPoint, ServiceMethod.DELETE, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!checkResultFromDataService(json))
                        {
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN;
                        }
                        if (!result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        LogError("deleteConsultAssetWithConsultID", result);
                        return result;
                    }
                }, completion);
    }

    public void addConsultAssetWithUri(final String uri, final AssetType type,
                                       final String consultID,
                                       final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ADD_CONSULT_ASSET_ENDPOINT,
                        mSharecareToken.accountID, consultID);

        // Create request body.
        final String assetType = type == AssetType.Video ? VIDEO : IMAGE;
        final HashMap<String, String> body = new HashMap<String, String>(2);
        body.put(TYPE, assetType);
        body.put(URI, uri);

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.POST, headers, null,
                bodyJson, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!checkResultFromDataService(json))
                        {
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_UNKNOWN;
                        }
                        if (!result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] medication methods

    /**
     * Attempt to retrieve a list of medications.
     *
     * @param completion
     *            if successful, the medications are stored in the
     *            ResponseResult's parameters property, under the key
     *            "medications".
     */
    public void getMedicationsWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_MEDICATIONS_ENDPOINT, mSharecareToken.accountID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (checkResultFromDataService(json))
                            {
                                if (response != null && response.isJsonArray())
                                {
                                    final ArrayList<Medication> medications =
                                            new ArrayList<Medication>();
                                    for (final JsonElement element : response
                                            .getAsJsonArray())
                                    {
                                        if (element.isJsonObject())
                                        {
                                            final JsonObject medicationJson =
                                                    element.getAsJsonObject();
                                            final String identifier =
                                                    getStringFromJson(medicationJson,
                                                            ID);
                                            final String name =
                                                    getStringFromJson(medicationJson,
                                                            NAME);
                                            if (!StringHelper
                                                    .isNullOrEmpty(identifier)
                                                    && !StringHelper
                                                    .isNullOrEmpty(name))
                                            {
                                                final Medication medication =
                                                        new Medication(identifier, name);
                                                medications.add(medication);
                                            }
                                        }
                                    }
                                    final HashMap<String, Object> parameters =
                                            new HashMap<String, Object>(1);
                                    parameters.put("medications", medications);
                                    result.parameters = parameters;
                                }
                                else if (response != null)
                                {
                                    try
                                    {
                                        result.errorMessage =
                                                response.getAsString();
                                    }
                                    catch (final ClassCastException e)
                                    {
//                                        Crashlytics.logException(e);
                                    }
                                    result.success = false;
                                    result.responseCode =
                                            ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                                }
                            }
                            else if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                                result.success = false;
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                            }
                        }
                        LogError("getMedicationsWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    public void addMedication(final Medication medication,
                              final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ADD_MEDICATION_ENDPOINT, mSharecareToken.accountID);

        // Create request body.
        final HashMap<String, String> body = new HashMap<String, String>(2);
        body.put(ID, medication.id);
        body.put(NAME, medication.getName());

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!checkResultFromDataService(json))
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                        }
                        LogError("addMedication", result);
                        return result;
                    }
                }, completion);
    }

    public void deleteMedicationByID(final String medicationID,
                                     final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(DELETE_MEDICATION_ENDPOINT,
                        mSharecareToken.accountID, medicationID);
        this.beginRequest(endPoint, ServiceMethod.DELETE, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!checkResultFromDataService(json))
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                        }
                        LogError("deleteMedicationByID", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] secondary medication methods

    /**
     * Attempt to retrieve a list of medications associated with the given
     * secondary profile.
     *
     * @param secondary
     * @param completion
     *            if successful, the list of medications are stored in the
     *            ResponseResult's parameters property, under the key
     *            "medications".
     */
    public void getMedicationsWithSecondary(final SecondaryProfile secondary,
                                            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_SECONDARY_MEDICATIONS_ENDPOINT,
                        mSharecareToken.accountID, secondary.identifier);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success && checkResultFromDataService(json))
                        {
                            if (response != null && response.isJsonArray())
                            {
                                final ArrayList<Medication> medications =
                                        new ArrayList<Medication>();
                                for (final JsonElement element : response
                                        .getAsJsonArray())
                                {
                                    if (element.isJsonObject())
                                    {
                                        final JsonObject medicationJson =
                                                element.getAsJsonObject();
                                        final String identifier =
                                                getStringFromJson(medicationJson, ID);
                                        final String name =
                                                getStringFromJson(medicationJson, NAME);
                                        if (!StringHelper.isNullOrEmpty(identifier)
                                                && !StringHelper.isNullOrEmpty(name))
                                        {
                                            final Medication medication =
                                                    new Medication(identifier, name);
                                            medications.add(medication);
                                        }
                                    }
                                }
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put("medications", medications);
                                result.parameters = parameters;
                            }
                            else
                            {
                                try
                                {
                                    if (response != null)
                                    {
                                        result.errorMessage =
                                                response.getAsString();
                                    }
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                                result.success = false;
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                            }
                        }
                        else
                        {
                            try
                            {
                                if (response != null)
                                {
                                    result.errorMessage = response.getAsString();
                                }
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                        }
                        LogError("getMedicationsWithSecondary", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to add the given medication to the given secondary profile.
     *
     * @param secondary
     * @param medication
     * @param completion
     *            indicates request success or error messages.
     */
    public void addMedicationWithSecondary(final SecondaryProfile secondary,
                                           final Medication medication,
                                           final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ADD_SECONDARY_MEDICATION_ENDPOINT,
                        mSharecareToken.accountID, secondary.identifier);

        // Create request body.
        final HashMap<String, String> body = new HashMap<String, String>(2);
        body.put(ID, medication.id);
        body.put(NAME, medication.getName());

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!checkResultFromDataService(json))
                        {
                            try
                            {
                                final JsonElement response =
                                        getResponseFromJson(json);
                                if (response != null)
                                {
                                    result.errorMessage = response.getAsString();
                                }
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                        }
                        LogError("addMedicationWithSecondary", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to delete the medication with the given medicationID from the
     * given secondary profile.
     *
     * @param secondary
     * @param medicationID
     * @param completion
     *            indicates request success or error messages.
     */
    public void deleteMedicationWithSecondary(final SecondaryProfile secondary,
                                              final String medicationID,
                                              final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(DELETE_SECONDARY_MEDICATION_ENDPOINT,
                        mSharecareToken.accountID, secondary.identifier, medicationID);
        this.beginRequest(endPoint, ServiceMethod.DELETE, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        if (!checkResultFromDataService(json))
                        {
                            try
                            {
                                final JsonElement response =
                                        getResponseFromJson(json);
                                if (response != null)
                                {
                                    result.errorMessage = response.getAsString();
                                }
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                            result.success = false;
                            result.responseCode =
                                    ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                        }
                        LogError("deleteMedicationWithSecondary", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // TODO: Implement public asset methods.

    // [region] condition methods

    /**
     * Attempt to retrieve the list of medical conditions.
     *
     * @param completion
     *            if successful, the conditions are stored in the
     *            ResponseResult's parameters property, under the key
     *            MEDICAL_CONDITIONS.
     */
    public void getConditionListWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        this.beginRequest(GET_CONDITIONS_ENDPOINT, ServiceMethod.GET, headers,
                null, (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success)
                        {
                            if (response != null && response.isJsonArray())
                            {
                                final ArrayList<MedicalCondition> conditions =
                                        new ArrayList<MedicalCondition>();
                                for (final JsonElement element : response
                                        .getAsJsonArray())
                                {
                                    if (element.isJsonObject())
                                    {
                                        final JsonObject conditionJson =
                                                element.getAsJsonObject();
                                        final String identifier =
                                                getStringFromJson(conditionJson, ID);
                                        final String name =
                                                getStringFromJson(conditionJson, NAME);
                                        final MedicalCondition condition =
                                                new MedicalCondition(identifier, name);
                                        conditions.add(condition);
                                    }
                                }
                                if (conditions.size() > 0)
                                {
                                    final HashMap<String, Object> parameters =
                                            new HashMap<String, Object>(1);
                                    parameters.put(MEDICAL_CONDITIONS, conditions);
                                    result.parameters = parameters;
                                }
                            }
                        }
                        else
                        {
                            if (response != null)
                            {
                                try
                                {
                                    result.errorMessage = response.getAsString();
                                }
                                catch (final ClassCastException e)
                                {
//                                    Crashlytics.logException(e);
                                }
                            }
                        }
                        LogError("getConditionListWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    // [endregion]

    // [region] physician and hospital methods

    /**
     * Attempt to retrieve a list of specialties by topic ID.
     *
     * @param topicID
     * @param completion
     *            if successful, the list of specialties is stored in the
     *            ResponseResult's parameters property, under the key
     *            "specialties".
     */
    public void getSpecialtiesByTopicID(final String topicID,
                                        final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_SPECIALTIES_ENDPOINT, topicID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success && response != null
                                && response.isJsonArray())
                        {
                            final ArrayList<Specialty> specialties =
                                    new ArrayList<Specialty>();
                            for (final JsonElement element : response
                                    .getAsJsonArray())
                            {
                                if (element.isJsonObject())
                                {
                                    final JsonObject specialtyJson =
                                            element.getAsJsonObject();
                                    final String identifier =
                                            getStringFromJson(specialtyJson, ID);
                                    final String name =
                                            getStringFromJson(specialtyJson, NAME);
                                    final ArrayList<SpecialtyTag> tags =
                                            new ArrayList<SpecialtyTag>();
                                    if (getJsonArrayFromJson(specialtyJson, TAGS) != null)
                                    {
                                        for (final JsonElement tagElement : getJsonArrayFromJson(
                                                specialtyJson, TAGS))
                                        {
                                            if (tagElement.isJsonObject())
                                            {
                                                final JsonObject tagJson =
                                                        tagElement.getAsJsonObject();
                                                final String tagID =
                                                        getStringFromJson(tagJson, ID);
                                                final String tagType =
                                                        getStringFromJson(tagJson, TYPE);
                                                final SpecialtyTag tag =
                                                        new SpecialtyTag(tagID, tagType);
                                                tags.add(tag);
                                            }
                                        }
                                    }
                                    final Specialty specialty =
                                            new Specialty(identifier, name, tags);
                                    specialties.add(specialty);
                                }
                            }
                            if (specialties.size() > 0)
                            {
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put("specialties", specialties);
                                result.parameters = parameters;
                            }
                        }
                        else if (response != null)
                        {
                            try
                            {
                                result.errorMessage = response.getAsString();
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                        }
                        LogError("getSpecialtiesByTopicID", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to retrieve a list of insurance carriers.
     *
     * @param completion
     *            if successful, the list of insurance carriers is stored in the
     *            ResponseResult's parameters property, under the key
     *            "insurance".
     */
    public void getInsuranceCarriersWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        this.beginRequest(GET_INSURANCE_CARRIERS_ENDPOINT, ServiceMethod.GET,
                headers, null, (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success && response != null
                                && response.isJsonArray())
                        {
                            final ArrayList<InsuranceCarrier> carriers =
                                    new ArrayList<InsuranceCarrier>();
                            for (final JsonElement element : response
                                    .getAsJsonArray())
                            {
                                if (element.isJsonObject())
                                {
                                    final JsonObject carrierJson =
                                            element.getAsJsonObject();
                                    final String identifier =
                                            getStringFromJson(carrierJson, ID);
                                    final String name =
                                            getStringFromJson(carrierJson, NAME);
                                    final InsuranceCarrier carrier =
                                            new InsuranceCarrier(identifier, name);
                                    carriers.add(carrier);
                                }
                            }
                            if (carriers.size() > 0)
                            {
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put(INSURANCE, carriers);
                                result.parameters = parameters;
                            }
                        }
                        else if (response != null)
                        {
                            try
                            {
                                result.errorMessage = response.getAsString();
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                        }
                        LogError("getInsuranceCarriersWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    /**
     * Attempt to retrieve the insurance plans associated with the carrier with
     * the given carrierID.
     *
     * @param carrierID
     * @param completion
     *            if successful, the insurance plans are stored in the
     *            ResponseResult's parameters property, under the key
     *            "insurance".
     */
    public void getInsurancePlansByCarrierID(final String carrierID,
                                             final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_INSURANCE_PLANS_ENDPOINT, carrierID);
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        if (result.success && response != null
                                && response.isJsonArray())
                        {
                            final ArrayList<InsurancePlan> plans =
                                    new ArrayList<InsurancePlan>();
                            for (final JsonElement element : response
                                    .getAsJsonArray())
                            {
                                if (element.isJsonObject())
                                {
                                    final JsonObject planJson =
                                            element.getAsJsonObject();
                                    final String identifier =
                                            getStringFromJson(planJson, ID);
                                    final String name =
                                            getStringFromJson(planJson, NAME);
                                    final InsurancePlan plan =
                                            new InsurancePlan(identifier, name);
                                    plans.add(plan);
                                }
                            }
                            if (plans.size() > 0)
                            {
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put(INSURANCE, plans);
                                result.parameters = parameters;
                            }
                        }
                        else if (response != null)
                        {
                            try
                            {
                                result.errorMessage = response.getAsString();
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                        }
                        LogError("getInsurancePlansByCarrierID", result);
                        return result;
                    }
                }, completion);
    }

    public void getPhysiciansWithLocationNameAndSpecialtyName(
            final String locationName, final String specialtyName,
            final boolean sponsors,
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                sponsors
                        ? FIND_SPONSORED_PHYSICIANS_ENDPOINT
                        : FIND_PHYSICIANS_ENDPOINT;

        // Create request parameters.
        final HashMap<String, String> queryParameters =
                new HashMap<String, String>(4);
        queryParameters.put(APPLICATION, ASKMD);
        queryParameters.put(SPECIALTY, specialtyName);
        queryParameters.put(LOCATION_QUERY, locationName);
        queryParameters.put(CREDENTIALS, MD_DDS);

        this.beginRequest(endPoint, ServiceMethod.GET, headers,
                queryParameters, (String)null, ServiceResponseFormat.GSON,
                getPhysiciansResponseTransform(sponsors), completion);
    }

    public void getPhysiciansWithLongitudeLatitudeAndSpecialtyName(
            final double lng, final double lat, final String specialtyName,
            final boolean sponsors,
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                sponsors
                        ? FIND_SPONSORED_PHYSICIANS_ENDPOINT
                        : FIND_PHYSICIANS_ENDPOINT;

        // Create request parameters.
        final String location = String.valueOf(lat) + "," + String.valueOf(lng);
        final HashMap<String, String> queryParameters =
                new HashMap<String, String>(4);
        queryParameters.put(APPLICATION, ASKMD);
        queryParameters.put(SPECIALTY, specialtyName);
        queryParameters.put(LOCATION, location);
        queryParameters.put(CREDENTIALS, MD_DDS);

        this.beginRequest(endPoint, ServiceMethod.GET, headers,
                queryParameters, (String)null, ServiceResponseFormat.GSON,
                getPhysiciansResponseTransform(sponsors), completion);
    }

    public void getPhysiciansWithLocationNameAndTopicID(
            final String locationName, final String topicID,
            final boolean sponsors,
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                sponsors
                        ? FIND_SPONSORED_PHYSICIANS_ENDPOINT
                        : FIND_PHYSICIANS_ENDPOINT;

        // Create request parameters.
        final HashMap<String, String> queryParameters =
                new HashMap<String, String>(4);
        queryParameters.put(APPLICATION, ASKMD);
        queryParameters.put(CPNUM, topicID);
        queryParameters.put(LOCATION_QUERY, locationName);
        queryParameters.put(CREDENTIALS, MD_DDS);

        this.beginRequest(endPoint, ServiceMethod.GET, headers,
                queryParameters, (String)null, ServiceResponseFormat.GSON,
                getPhysiciansResponseTransform(sponsors), completion);
    }

    public void getPhysiciansWithLocationNameTopicIDSponsorsAndSpecialties(
            final String locationName, final String topicID, final String sponsors,
            final List<String> specialties,
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        String endPoint = null;

        // Create request parameters.
        String specialtiesString = null;
        if (specialties != null)
        {
            for (final String specialty : specialties)
            {
                if (specialtiesString == null)
                {
                    specialtiesString = specialty.toLowerCase(Locale.US);
                }
                else
                {
                    specialtiesString =
                            specialtiesString + ","
                                    + specialty.toLowerCase(Locale.US);
                }
            }
        }
        final HashMap<String, String> queryParameters =
                new HashMap<String, String>(4);
        queryParameters.put(LOCATION_QUERY, locationName);
        queryParameters.put(CREDENTIALS, MD_DDS);
        if (StringHelper.isNullOrEmpty(sponsors))
        {
            endPoint = FIND_PHYSICIANS_ENDPOINT;
            queryParameters.put(HOSPITAL_EXCLAMATION, "");
        }
        else
        {
            endPoint = FIND_SPONSORED_PHYSICIANS_ENDPOINT;
            queryParameters.put(HOSPITAL, sponsors);
        }
        if (StringHelper.isNullOrEmpty(specialtiesString))
        {
            queryParameters.put(CPNUM, topicID);
        }
        else
        {
            queryParameters.put(SPECIALTY, specialtiesString);
        }

        this.beginRequest(endPoint, ServiceMethod.GET, headers,
                queryParameters, (String)null, ServiceResponseFormat.GSON,
                getPhysiciansResponseTransform(!StringHelper
                        .isNullOrEmpty(sponsors)), completion);
    }

    public void getPhysiciansWithLongitudeLatitudeTopicIDSponsorsAndSpecialties(
            final double lng, final double lat, final String topicID,
            final String sponsors, final List<String> specialties,
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        String endPoint = null;

        // Create request parameters.
        final String location = String.valueOf(lat) + "," + String.valueOf(lng);
        String specialtiesString = null;
        if (specialties != null)
        {
            for (final String specialty : specialties)
            {
                if (specialtiesString == null)
                {
                    specialtiesString = specialty.toLowerCase(Locale.US);
                }
                else
                {
                    specialtiesString =
                            specialtiesString + ","
                                    + specialty.toLowerCase(Locale.US);
                }
            }
        }
        final HashMap<String, String> queryParameters =
                new HashMap<String, String>(4);
        queryParameters.put(LOCATION, location);
        queryParameters.put(CREDENTIALS, MD_DDS);
        if (StringHelper.isNullOrEmpty(sponsors))
        {
            endPoint = FIND_PHYSICIANS_ENDPOINT;
            queryParameters.put(HOSPITAL_EXCLAMATION, "");
        }
        else
        {
            endPoint = FIND_SPONSORED_PHYSICIANS_ENDPOINT;
            queryParameters.put(HOSPITAL, sponsors);
        }
        if (StringHelper.isNullOrEmpty(specialtiesString))
        {
            queryParameters.put(CPNUM, topicID);
        }
        else
        {
            queryParameters.put(SPECIALTY, specialtiesString);
        }

        this.beginRequest(endPoint, ServiceMethod.GET, headers,
                queryParameters, (String)null, ServiceResponseFormat.GSON,
                getPhysiciansResponseTransform(!StringHelper
                        .isNullOrEmpty(sponsors)), completion);
    }

    public void getHospitalWithLocationName(final String locationName,
                                            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);

        // Create endpoint.
        final String encodedLocationName = urlEncodeParameterData(locationName);
        final String endPoint =
                String.format(GET_HOSPITAL_BY_LOCATION_NAME_ENDPOINT,
                        encodedLocationName);

        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                getHospitalResponseTransform(), completion);
    }

    public void getHospitalWithLongitudeAndLatitude(final double lng,
                                                    final double lat,
                                                    final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(GET_HOSPITAL_BY_LOCATION_ENDPOINT,
                        String.valueOf(lat), String.valueOf(lng));
        this.beginRequest(endPoint, ServiceMethod.GET, headers, null,
                (String)null, ServiceResponseFormat.GSON,
                getHospitalResponseTransform(), completion);
    }

    public boolean reHydrateSharecareToken(final JsonElement json)
    {
        if (json == null)
        {
            return false;
        }

        final JsonObject obj = json.getAsJsonObject();
        final String accessToken = obj.get("accessToken").getAsString();
        final String tokenType = obj.get("tokenType").getAsString();

        Date expiresIn = null;
        final JsonElement element = obj.get("expiresIn");
        if (element != null)
        {
            final String dateString = element.getAsString();
            try
            {
                expiresIn =
                        new SimpleDateFormat("MMMM d, yyyy hh:mm:ss a",
                                Locale.ENGLISH).parse(dateString);
            }
            catch (final ParseException e)
            {
//                Crashlytics.logException(e);
            }
        }

        final String refreshToken = obj.get("refreshToken").getAsString();
        final String accountId = obj.get("accountID").getAsString();

        final boolean askMDProfileCreated =
                obj.get("askMDProfileCreated").getAsBoolean();
        final boolean preProfileCreation =
                obj.get("preProfileCreation").getAsBoolean();

        if (mSharecareToken == null)
        {
            mSharecareToken =
                    new SharecareToken(accessToken, tokenType, expiresIn,
                            refreshToken, accountId, askMDProfileCreated,
                            preProfileCreation);
        }
        else
        {
            mSharecareToken.accessToken = accessToken;
            mSharecareToken.tokenType = tokenType;
            mSharecareToken.expiresIn = expiresIn;
            mSharecareToken.refreshToken = refreshToken;
            mSharecareToken.accountID = accountId;
            mSharecareToken.askMDProfileCreated = askMDProfileCreated;
            mSharecareToken.preProfileCreation = preProfileCreation;
        }

        return true;
    }

    // [endregion]

    // [region] private methods

    /**
     * Attempt to create a SharecareToken from parameters in the JSON.
     *
     * @param json
     * @param askMDProfileCreated
     * @param preProfileCreation
     * @return true if successful, false if not.
     */
    private synchronized boolean setSharecareToken(final JsonElement json,
                                                   final boolean askMDProfileCreated, final boolean preProfileCreation)
    {
        if (json == null)
        {
            return false;
        }
        final JsonObject obj = json.getAsJsonObject();
        final String accessToken = obj.get("access_token").getAsString();
        final String tokenType = obj.get("token_type").getAsString();

        Date expiresIn = null;
        if (obj.get("expires_in") != null)
        {
            final long expiresInMilliseconds =
                    (long)(obj.get("expires_in").getAsDouble() * 1000.0);
            final Date now = new Date();
            expiresIn = new Date(now.getTime() + expiresInMilliseconds);
        }

        final String refreshToken = obj.get("refresh_token").getAsString();
        final String accountId = obj.get("account_id").getAsString();

        if (StringHelper.isNullOrEmpty(accessToken)
                || StringHelper.isNullOrEmpty(tokenType) || expiresIn == null
                || StringHelper.isNullOrEmpty(accountId))
        {
            return false;
        }

        if (mSharecareToken == null)
        {
            mSharecareToken =
                    new SharecareToken(accessToken, tokenType, expiresIn,
                            refreshToken, accountId, askMDProfileCreated,
                            preProfileCreation);
        }
        else
        {
            mSharecareToken.accessToken = accessToken;
            mSharecareToken.tokenType = tokenType;
            mSharecareToken.expiresIn = expiresIn;
            mSharecareToken.refreshToken = refreshToken;
            mSharecareToken.accountID = accountId;
            mSharecareToken.askMDProfileCreated = askMDProfileCreated;
            mSharecareToken.preProfileCreation = preProfileCreation;
        }

        // Save the sharecareToken to the settings manager.
        SettingsManager.instance.sharecareToken = mSharecareToken;

        return true;
    }

    /**
     * Update the SharecareToken to show that the askMDProfile has been created.
     */
    private void setAskMDProfileCreatedInSharecareToken()
    {
        mSharecareToken.askMDProfileCreated = true;
        mSharecareToken.preProfileCreation = false;
        // TODO: Save the sharecareToken to the as of yet uncreated settings
        // manager.
    }

    /**
     * Attempt to update the user's AskMD profile. Note that completion may be
     * invoked more than once due to a nested call.
     *
     * @param completion
     *            if successful, the updated profile is stored in the
     *            ResponseResult's parameters property, under the key "profile".
     */
    private void updateAskMDProfileWithCompletion(
            final ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(ASKMD_PROFILE_ENDPOINT, mSharecareToken.accountID);

        // Create request body.
        final HashMap<String, String> body = new HashMap<String, String>(1);
        body.put(HAS_ACCEPTED_TERMS_AND_CONDITIONS, "true");

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        // Create request method.
        ServiceMethod method;
        if (reloadTerms)
        {
            method = ServiceMethod.POST;
        }
        else
        {
            method = ServiceMethod.PUT;
        }

        this.beginRequest(endPoint, method, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonElement response = getResponseFromJson(json);
                        final Profile profile =
                                ProfileManager._profileManagerInstance.profile;
                        if (result.success)
                        {
                            reloadTerms = false;
                            if (!checkResultFromDataService(json))
                            {
                                if (response != null)
                                {
                                    try
                                    {
                                        result.errorMessage =
                                                response.getAsString();
                                    }
                                    catch (final ClassCastException e)
                                    {
//                                        Crashlytics.logException(e);
                                    }
                                }
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                                reloadTerms = true;
                                updateAskMDProfileWithCompletion(null);
                            }
                            else
                            {
                                setAskMDProfileCreatedInSharecareToken();
                                if (profile != null)
                                {
                                    getProfileWithCompletion(new ServiceClientCompletion<ResponseResult>()
                                    {
                                        @Override
                                        public void onCompletion(
                                                final ServiceResultStatus serviceResultStatus,
                                                final int responseCode,
                                                final ResponseResult resultValue)
                                        {
                                            final Profile profile =
                                                    (Profile)resultValue.parameters
                                                            .get(PROFILE);
                                            if (profile != null
                                                    && completion != null)
                                            {
                                                completion.onCompletion(
                                                        serviceResultStatus,
                                                        responseCode, resultValue);
                                                return;
                                            }
                                        }
                                    });
                                    return result;
                                }
                            }
                        }
                        final HashMap<String, Object> parameters =
                                new HashMap<String, Object>(1);
                        parameters.put(PROFILE, profile);
                        result.parameters = parameters;
                        LogError("updateAskMDProfileWithCompletion", result);
                        return result;
                    }
                }, completion);
    }

    private String createConsultDataStringForConsult(
            final Consultation consultation, final boolean privateData)
    {
        final HashMap<String, Object> documentData =
                new HashMap<String, Object>();
        documentData.put(CATEGORY, consultation.topic.topicCategory);
        documentData.put(TOPIC_ID, consultation.topic.topicId);
        if (privateData)
        {
            documentData.put(PHYSICIANS_CALLED, consultation.physiciansCalled);
            documentData.put(CHECKED_MEDS, consultation.checkedMeds);
            final String notes =
                    StringHelper.isNullOrEmpty(consultation.note)
                            ? ""
                            : consultation.note;
            documentData.put(NOTES, notes);
            if (consultation.honeycombLogic != null
                    && consultation.honeycombLogic.markedIndices != null)
            {
                documentData.put(MARKED_INDICES,
                        consultation.honeycombLogic.markedIndices.keySet());
            }
        }
        else
        {
            final HashMap<String, ArrayList<String>> flaggedResultsToCategory =
                    new HashMap<String, ArrayList<String>>();
            if (consultation.topic.type != TopicType.NO_GUIDANCE)
            {
                for (final ResultCategory causeCategory : consultation.causeCategories)
                {
                    final ArrayList<String> flaggedResults =
                            new ArrayList<String>();
                    for (final Result cause : causeCategory.results)
                    {
                        if (cause.flagged)
                        {
                            flaggedResults.add(cause.entNo);
                        }
                    }
                    flaggedResultsToCategory.put(causeCategory.name,
                            flaggedResults);
                }
            }
            documentData.put(RESULT_FLAGS, flaggedResultsToCategory);
        }

        final Gson gson = new GsonBuilder().create();
        return gson.toJson(documentData);
    }

    private HashMap<String, String> createMetadataForConsultSummary(
            final ConsultSummary consultSummary, final boolean forEdit)
    {
        final long timeInterval =
                forEdit ? consultSummary.getDateAdded().getTime() : new Date()
                        .getTime();
        final String name =
                StringHelper.isNullOrEmpty(consultSummary.name)
                        ? ""
                        : consultSummary.name;
        final HashMap<String, String> metadataDictionary =
                new HashMap<String, String>();
        metadataDictionary.put(REV, consultSummary.topicRev);
        metadataDictionary.put(TOPIC_ID, consultSummary.topicID);
        metadataDictionary.put(INFOCARD_ID, consultSummary.infocardID);
        metadataDictionary.put(CATEGORY, consultSummary.topicCategory);
        metadataDictionary.put(DATE_ADDED, String.valueOf(timeInterval));
        metadataDictionary.put(NAME, name);
        metadataDictionary.put(NUM_CAUSES_FLAGGED,
                String.valueOf(consultSummary.numCausesFlagged));
        metadataDictionary.put(NUM_PHYSICIANS_CALLED,
                String.valueOf(consultSummary.numPhysiciansCalled));
        metadataDictionary
                .put(HAS_NOTE, String.valueOf(consultSummary.hasNote));
        metadataDictionary.put(VERSION, SERVICE_CLIENT_VERSION);
        metadataDictionary.put(TYPE, USER_DATA);
        if (consultSummary.getProfileID() != null)
        {
            metadataDictionary.put(PROFILE_ID, consultSummary.getProfileID());
        }
        if (forEdit)
        {
            metadataDictionary.put(USER_DATA_PRIVATE_DOC_ID,
                    consultSummary.userDocPrivateID);
            metadataDictionary.put(SESSION_DOC_ID, consultSummary.sessionDocID);
            metadataDictionary.put(POPT_LIST_DOC_ID,
                    consultSummary.poptListDocID);
            metadataDictionary
                    .put(FND_LIST_DOC_ID, consultSummary.fndListDocID);
        }
        return metadataDictionary;
    }

    /**
     * Attempt to add/edit a document for the given consultation.
     *
     * @param consultation
     * @param documentType
     * @param forEdit
     * @param completion
     *            if successful, the document ID is stored in the
     *            ResponseResult's parameters property, under the key "id".
     * @throws InvalidDocumentTypeException
     */
    private void saveDocumentForConsult(final Consultation consultation,
                                        final ConsultSummary.ConsultSummaryDocumentType documentType, final boolean forEdit,
                                        final ServiceClientCompletion<ResponseResult> completion)
            throws ConsultSummary.InvalidDocumentTypeException
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);

        String documentString = null;
        String type = null;
        String docID = null;
        switch (documentType)
        {
            case UserDoc:
                type = USER_DATA;
                documentString =
                        createConsultDataStringForConsult(consultation, false);
                docID = consultation.consultSummary.userDocID;
                break;
            case UserDocPrivate:
                type = USER_DATA_PRIVATE;
                documentString =
                        createConsultDataStringForConsult(consultation, true);
                docID = consultation.consultSummary.userDocPrivateID;
                break;
            case FndList:
                type = FND_LIST;
                documentString = consultation.fndListDocString;
                docID = consultation.consultSummary.fndListDocID;
                break;
            case PoptList:
                type = POPT_LIST;
                documentString = consultation.poptListDocString;
                docID = consultation.consultSummary.poptListDocID;
                break;
            case Session:
                type = SESSION;
                documentString = consultation.sessionDocString;
                docID = consultation.consultSummary.sessionDocID;
                break;
            case None:
//                Crashlytics
//                        .log("Invalid document type: ConsultSummaryDocumentType.None");
                throw new ConsultSummary.InvalidDocumentTypeException(
                        "Invalid document type: ConsultSummaryDocumentType.None");
        }

        // Set the metadata dictionary.
        HashMap<String, String> metadataDictionary;
        if (documentType == ConsultSummary.ConsultSummaryDocumentType.UserDoc)
        {
            metadataDictionary =
                    createMetadataForConsultSummary(consultation.consultSummary,
                            forEdit);
        }
        else
        {
            metadataDictionary = new HashMap<String, String>(2);
            metadataDictionary.put(VERSION, SERVICE_CLIENT_VERSION);
            metadataDictionary.put(TYPE, type);
        }

        // Create request body.
        final HashMap<String, Object> body = new HashMap<String, Object>(3);
        body.put(TITLE, consultation.consultSummary.getTopicName());
        body.put(DOCUMENT, documentString);
        body.put(METADATA, metadataDictionary);

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        // Create endpoint.
        final String endPoint =
                forEdit ? String.format(EDIT_CONSULT_ENDPOINT,
                        mSharecareToken.accountID, docID) : String.format(
                        ADD_DOCUMENT_ENDPOINT, mSharecareToken.accountID);

        this.beginRequest(endPoint, forEdit
                        ? ServiceMethod.POST
                        : ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        String docID = null;
                        if (result.success)
                        {
                            final JsonElement response = getResponseFromJson(json);
                            if (response != null && response.isJsonObject())
                            {
                                final JsonObject responseJson =
                                        response.getAsJsonObject();
                                docID = getStringFromJson(responseJson, ID);
                            }

                            if (StringHelper.isNullOrEmpty(docID))
                            {
                                if (response != null)
                                {
                                    try
                                    {
                                        result.errorMessage =
                                                response.getAsString();
                                    }
                                    catch (final ClassCastException e)
                                    {
//                                        Crashlytics.logException(e);
                                    }
                                }
                                result.success = false;
                                result.responseCode =
                                        ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                            }
                            else
                            {
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put(ID, docID);
                                result.parameters = parameters;
                            }
                        }
                        LogError("saveDocumentForConsult", result);
                        return result;
                    }
                }, completion);
    }

    private void emailConsult(final String consultID, final String userID,
                              final String shareToName, final String shareToEmail,
                              final boolean isProvider,
                              final ServiceClientCompletion<ResponseResult> completion)
    {
        // Create request headers.
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);

        // Create request body.
        final HashMap<String, String> body = new HashMap<String, String>();
        if (!StringHelper.isNullOrEmpty(userID))
        {
            body.put(STATUS, "EXISTING");
            body.put(ID, userID);
        }
        else
        {
            body.put(STATUS, "PENDING");
            body.put(NAME, shareToName);
            body.put(EMAIL, shareToEmail);
            body.put(PROVIDER, isProvider ? "true" : "false");
        }

        final Gson gson = new GsonBuilder().create();
        final String bodyJson = gson.toJson(body);

        // Create endpoint.
        final String endPoint =
                String.format(EMAIL_CONSULT_ENDPOINT, mSharecareToken.accountID,
                        consultID);

        this.beginRequest(endPoint, ServiceMethod.PUT, headers, null, bodyJson,
                ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {
                    @Override
                    public ResponseResult transformResponseData(
                            final JsonElement json)
                            throws ServiceResponseTransformException
                    {
                        final ResponseResult result =
                                checkResultFromAuthService(json);
                        final JsonObject jsonObject = json.getAsJsonObject();
                        final String resultFromJson =
                                getStringFromJson(jsonObject, "result");
                        if (!result.success
                                || (resultFromJson != null && resultFromJson
                                .equals("FAILURE")))
                        {
                            result.success = false;
                            if (resultFromJson != null
                                    && resultFromJson.equals("FAILURE"))
                            {
                                result.errorMessage =
                                        getStringFromJson(jsonObject,
                                                SERVICECLIENT_ERROR_MESSAGE_KEY);
                            }
                        }
                        LogError("emailConsult", result);
                        return result;
                    }
                }, completion);
    }

    private String getAssetSizeString(final AssetSize assetSize)
    {
        String assetSizeString = "";
        switch (assetSize)
        {
            case Size300x300:
                assetSizeString = "SIZE_300x300";
                break;
            case Size240x240:
                assetSizeString = "SIZE_240x240";
                break;
            case Size120x120:
                assetSizeString = "SIZE_120x120";
                break;
            case Size290x290:
                assetSizeString = "SIZE_290x290";
                break;
            case Size298x248:
                assetSizeString = "SIZE_298x248";
                break;
            case Size60x60:
                assetSizeString = "SIZE_60x60";
                break;
            default:
                break;
        }
        return assetSizeString;
    }

    /**
     * Utility method used to generate ServiceResponseTransforms for physician
     * queries.
     *
     * @param sponsors
     * @return a ServiceResponseTransform that parses JSON data and returns a
     *         list of physicians in the ResponseResult's parameters property,
     *         under the key "physician".
     */
    private ServiceResponseTransform<JsonElement, ResponseResult> getPhysiciansResponseTransform(
            final boolean sponsors)
    {
        return new ServiceResponseTransform<JsonElement, ResponseResult>()
        {
            @Override
            public ResponseResult transformResponseData(final JsonElement json)
                    throws ServiceResponseTransformException
            {
                final ResponseResult result = checkResultFromAuthService(json);
                final JsonElement response = getResponseFromJson(json);
                if (result.success)
                {
                    if (checkResultFromDataService(json))
                    {
                        if (response != null && response.isJsonArray())
                        {
                            final ArrayList<Physician> physicians =
                                    new ArrayList<Physician>();
                            for (final JsonElement element : response
                                    .getAsJsonArray())
                            {
                                if (element.isJsonObject())
                                {
                                    final JsonObject jsonObject =
                                            element.getAsJsonObject();
                                    // Append specialties separated by commas.
                                    String specialties = null;
                                    if (getJsonArrayFromJson(jsonObject,
                                            SPECIALTIES) != null)
                                    {
                                        for (final JsonElement specialtyElement : getJsonArrayFromJson(
                                                jsonObject, SPECIALTIES))
                                        {
                                            if (specialtyElement.isJsonObject())
                                            {
                                                final JsonObject specialtyJson =
                                                        specialtyElement
                                                                .getAsJsonObject();
                                                final String name =
                                                        getStringFromJson(
                                                                specialtyJson, NAME);
                                                if (!StringHelper
                                                        .isNullOrEmpty(name))
                                                {
                                                    if (specialties == null)
                                                    {
                                                        specialties = name;
                                                    }
                                                    else
                                                    {
                                                        specialties +=
                                                                ", " + name;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Get insurance plans and carriers.
                                    final ArrayList<InsurancePlanAndCarrier> insurancePlansAndCarriers =
                                            new ArrayList<InsurancePlanAndCarrier>();
                                    if (getJsonArrayFromJson(jsonObject,
                                            INSURANCE_PLANS) != null)
                                    {
                                        for (final JsonElement insuranceElement : getJsonArrayFromJson(
                                                jsonObject, INSURANCE_PLANS))
                                        {
                                            if (insuranceElement.isJsonObject())
                                            {
                                                final JsonObject insuranceJson =
                                                        insuranceElement
                                                                .getAsJsonObject();
                                                final String planId =
                                                        getStringFromJson(
                                                                insuranceJson, PLAN_ID);
                                                final String planName =
                                                        getStringFromJson(
                                                                insuranceJson,
                                                                PLAN_NAME);
                                                final String carrierId =
                                                        getStringFromJson(
                                                                insuranceJson,
                                                                CARRIER_ID);
                                                final String carrierName =
                                                        getStringFromJson(
                                                                insuranceJson,
                                                                CARRIER_NAME);
                                                final InsurancePlanAndCarrier insurancePlanAndCarrier =
                                                        new InsurancePlanAndCarrier(
                                                                planId, planName,
                                                                carrierId, carrierName);
                                                insurancePlansAndCarriers
                                                        .add(insurancePlanAndCarrier);
                                            }
                                        }
                                    }

                                    final String avatarURL =
                                            getStringFromJson(jsonObject,
                                                    AVATAR_URI);
                                    final String city =
                                            getStringFromJson(jsonObject, CITY);
                                    final String country =
                                            getStringFromJson(jsonObject, COUNTRY);
                                    final double distance =
                                            getDoubleFromJson(jsonObject, DISTANCE,
                                                    0);
                                    final String firstName =
                                            getStringFromJson(jsonObject,
                                                    FIRST_NAME);
                                    final String physicianID =
                                            getStringFromJson(jsonObject, ID);
                                    final String lastName =
                                            getStringFromJson(jsonObject, LAST_NAME);
                                    final double lat =
                                            getDoubleFromJson(jsonObject, LAT, 0);
                                    final double lng =
                                            getDoubleFromJson(jsonObject, LNG, 0);
                                    final String middleInitial =
                                            getStringFromJson(jsonObject,
                                                    MIDDLE_INITIAL);
                                    final String clinic =
                                            getStringFromJson(jsonObject,
                                                    PRACTICE_NAME);
                                    final String state =
                                            getStringFromJson(jsonObject, STATE);
                                    final String streetAddress1 =
                                            getStringFromJson(jsonObject,
                                                    STREET_ADDRESS_ONE);
                                    final String streetAddress2 =
                                            getStringFromJson(jsonObject,
                                                    STREET_ADDRESS_TWO);
                                    final String suffix =
                                            getStringFromJson(jsonObject, SUFFIX);
                                    final String zip =
                                            getStringFromJson(jsonObject, ZIP);

                                    // Parse raw phone number.
                                    String phoneNumber =
                                            getStringFromJson(jsonObject,
                                                    PHONE_NUMBER);
                                    phoneNumber =
                                            phoneNumber.replaceAll("[^\\d]", "");

                                    final OfficeLocation officeLocation =
                                            new OfficeLocation(lat, lng,
                                                    streetAddress1, streetAddress2,
                                                    city, state, zip, country);
                                    final Physician physician =
                                            new Physician(physicianID, firstName,
                                                    lastName, middleInitial, suffix,
                                                    avatarURL, specialties, clinic,
                                                    officeLocation, distance,
                                                    phoneNumber, sponsors,
                                                    insurancePlansAndCarriers);
                                    physicians.add(physician);
                                    if (sponsors && physicians.size() >= 3)
                                    {
                                        break;
                                    }
                                }
                            }
                            if (physicians.size() > 0)
                            {
                                // Removes duplicate physicians, I think.
                                @SuppressWarnings("unchecked")
                                final ArrayList<Physician> duplicate =
                                        (ArrayList<Physician>)physicians.clone();
                                for (final Physician physician : physicians)
                                {
                                    for (final Physician test : physicians)
                                    {
                                        if (physician.lastName != null
                                                && physician.lastName
                                                .equalsIgnoreCase(test.lastName)
                                                && physician.firstName != null
                                                && physician.firstName
                                                .equalsIgnoreCase(test.firstName))
                                        {
                                            if (physician != test)
                                            { // Remove
                                                // if
                                                // not
                                                // same
                                                // object
                                                // reference.
                                                duplicate.remove(physician);
                                            }
                                        }

                                    }
                                }
                                final HashMap<String, Object> parameters =
                                        new HashMap<String, Object>(1);
                                parameters.put(PHYSICIAN, duplicate);
                                result.parameters = parameters;
                            }
                        }
                    }
                    else
                    {
                        if (response != null)
                        {
                            try
                            {
                                result.errorMessage = response.getAsString();
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                        }
                        result.success = false;
                        result.responseCode =
                                ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                    }
                }
                else if (response != null)
                {
                    try
                    {
                        result.errorMessage = response.getAsString();
                    }
                    catch (final ClassCastException e)
                    {
//                        Crashlytics.logException(e);
                    }
                }
                LogError("getPhysiciansResponseTransform", result);
                return result;
            }
        };
    }

    /**
     * Utility method used to generate ServiceResponseTransforms for hospital
     * queries.
     *
     * @return a ServiceResponseTransform that parses JSON data and returns a
     *         hospital in the ResponseResult's parameters property, under the
     *         key "hospital".
     */
    private ServiceResponseTransform<JsonElement, ResponseResult> getHospitalResponseTransform()
    {
        return new ServiceResponseTransform<JsonElement, ResponseResult>()
        {
            @Override
            public ResponseResult transformResponseData(final JsonElement json)
                    throws ServiceResponseTransformException
            {
                final ResponseResult result = checkResultFromAuthService(json);
                final JsonElement response = getResponseFromJson(json);
                if (result.success)
                {
                    if (checkResultFromDataService(json))
                    {
                        if (response != null && response.isJsonObject())
                        {
                            final JsonObject jsonObject =
                                    response.getAsJsonObject();
                            final String name =
                                    getStringFromJson(jsonObject, NAME);
                            final String street =
                                    getStringFromJson(jsonObject, STREET);
                            final String city =
                                    getStringFromJson(jsonObject, CITY);
                            final String state =
                                    getStringFromJson(jsonObject, STATE);
                            final String zip =
                                    getStringFromJson(jsonObject, ZIP);
                            final String phone =
                                    getStringFromJson(jsonObject, PHONE);
                            double lat = 0;
                            double lng = 0;
                            final JsonObject geolocation =
                                    getJsonObjectFromJson(jsonObject, GEOLOCATION);
                            if (geolocation != null)
                            {
                                lat = getDoubleFromJson(geolocation, LAT, 0);
                                lng = getDoubleFromJson(geolocation, LNG, 0);
                            }
                            final Hospital hospital =
                                    new Hospital(name, street, city, state, zip,
                                            phone, lat, lng);
                            final HashMap<String, Object> parameters =
                                    new HashMap<String, Object>(1);
                            parameters.put(HOSPITAL, hospital);
                            result.parameters = parameters;
                        }
                    }
                    else
                    {
                        if (response != null)
                        {
                            try
                            {
                                result.errorMessage = response.getAsString();
                            }
                            catch (final ClassCastException e)
                            {
//                                Crashlytics.logException(e);
                            }
                        }
                        result.success = false;
                        result.responseCode =
                                ServiceClientConstants.SERVICE_RESPONSE_STATUS_CODE_SERVER_ERROR;
                    }
                }
                else if (response != null)
                {
                    try
                    {
                        result.errorMessage = response.getAsString();
                    }
                    catch (final ClassCastException e)
                    {
//                        Crashlytics.logException(e);
                    }
                }
                LogError("getHospitalResponseTransform", result);
                return result;
            }
        };
    }

    private String urlEncodeParameterData(final String unencodedData)
    {
        try
        {
            return URLEncoder.encode(unencodedData, "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
//            Crashlytics.logException(e);
            return "";
        }
    }

    public void getUserWithCompletion(
            ServiceClientCompletion<ResponseResult> completion)
    {
        final HashMap<String, String> headers =
                getHeaderWithAccessToken(mSharecareToken.accessToken);
        final String endPoint =
                String.format(PROFILE_URI_ENDPOINT, mSharecareToken.accountID);
        // Create request parameters.
        HashMap<String, String> parameters = new HashMap<String, String>(1);
        parameters.put("v2", "");

        this.beginRequest(endPoint, ServiceMethod.GET, headers, parameters,
                (String)null, ServiceResponseFormat.GSON,
                new ServiceResponseTransform<JsonElement, ResponseResult>()
                {

                    @Override
                    public ResponseResult transformResponseData(
                            JsonElement responseData)
                            throws ServiceResponseTransformException
                    {
                        // check service result and err msg, if any
                        final ResponseResult result =
                                checkResultFromAuthService(responseData);
                        LogError("getUserWithCompletion", result);

                        final JsonElement response =
                                getResponseFromJson(responseData);
                        if (response != null && response.isJsonObject())
                        {
                            final JsonObject responseJson =
                                    response.getAsJsonObject();
                            final JsonObject image =
                                    getJsonObjectFromJson(responseJson, "image");

                            final JsonObject locations =
                                    getJsonObjectFromJson(image, "locations");

                            String uri = getStringFromJson(image, URI);
                            String url = getStringFromJson(locations, "SIZE_60x60");

                            String completeUrl = String.format("https:%s", url);
                            Bitmap avatar = null;

                            if (completeUrl != null)
                            {
                                try
                                {
                                    final URL avatarUrl = new URL(completeUrl);
                                    if(!avatarUrl.toString().equalsIgnoreCase("https:null"))
                                    {
                                        avatar =
                                                BitmapFactory.decodeStream(avatarUrl
                                                        .openConnection().getInputStream());
                                    }
                                }
                                catch (final MalformedURLException me)
                                {
//                                    Crashlytics.logException(me);
                                }
                                catch (final UnknownHostException ue)
                                {
//                                    Crashlytics.logException(ue);
                                }
                                catch (final IOException ie)
                                {
//                                    Crashlytics.logException(ie);
                                }
                            }

                            final HashMap<String, Object> parameters =
                                    new HashMap<String, Object>(3);
                            parameters.put(URL, completeUrl);
                            parameters.put(URI, uri);
                            parameters.put(AVATAR, avatar);

                            result.parameters = parameters;
                        }
                        return result;
                    }
                }, completion);
    }

    // [endregion]
}