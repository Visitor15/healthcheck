package mobile.forged.com.health.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by visitor15 on 11/9/14.
 */
public class ResponseResult
{

    // [region] instance variables

    public boolean success;
    public String errorMessage;
    public int responseCode;
    public Map<String, Object> parameters;

    // [endregion]


    // [region] constructors

    public ResponseResult()
    {
        this.success = true;
        this.errorMessage = "";
        this.responseCode = 200;
        this.parameters = new HashMap<String, Object>();
    }

    public ResponseResult(boolean success, String errorMessage, int responseCode)
    {
        this.success = success;
        this.errorMessage = errorMessage;
        this.responseCode = responseCode;
        this.parameters = new HashMap<String, Object>();
    }

    // [endregion]




}