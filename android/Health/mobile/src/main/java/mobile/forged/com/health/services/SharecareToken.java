package mobile.forged.com.health.services;

import java.util.Date;

/**
 * Created by visitor15 on 11/9/14.
 */
public class SharecareToken
{
    // [region] public fields

    public  String accessToken;
    public  String tokenType;
    public  Date expiresIn;
    public  String refreshToken;
    public  String accountID;
    public  boolean askMDProfileCreated;
    public  boolean preProfileCreation;

    // [endregion]

    // [region] cctors

    public SharecareToken (String accessToken,
                           String tokenType,
                           Date expiresIn,
                           String refreshToken,
                           String accountID,
                           boolean askMDProfileCreated,
                           boolean preProfileCreation)
    {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.accountID = accountID;
        this.askMDProfileCreated = askMDProfileCreated;
        this.preProfileCreation = preProfileCreation;
    }

    // [endregion]

}