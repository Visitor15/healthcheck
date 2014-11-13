package mobile.forged.com.health.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nascentdigital.util.observing.Observable;
import com.nascentdigital.util.observing.ObservableField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import mobile.forged.com.health.HealthApplication;
import mobile.forged.com.health.services.SharecareClient;
import mobile.forged.com.health.services.SharecareToken;
import mobile.forged.com.health.utilities.EncryptionHelper;
import mobile.forged.com.health.utilities.StringHelper;

/**
 * Created by visitor15 on 11/12/14.
 */
public class SettingsManager extends Observable
{
    // [region] constants
    public static final int FIELD_DRAWER_USED = 1;
    public static final int FIELD_SHARECARE_TOKEN = 2;

    private static final String SETTING_DRAWER_USED =
            "setting.user_learned_drawer";
    public static final String SETTING_SHARECARE_TOKEN =
            "setting.user_sharecare_token";
    public static final String TOKEN_FILE = "user_sharecare_token.txt";

    private static final String PREF_PKC_ENDPOINT_FETCHED = "p.f";
    private static final String PREF_PKC_ENDPOINT_URL = "p.u";

    // [endregion]


    // [region] class variables

    public static final SettingsManager instance = new SettingsManager();
    private static final SharedPreferences _preferences;
    private static final EncryptionHelper _encryptionHelper;
    private static File tokenFile;
    private static FileInputStream fin;
    private static FileOutputStream fout;
    // [endregion]


    // [region] instance variables

    @ObservableField(FIELD_DRAWER_USED)
    public boolean drawerLearned;

    @ObservableField(FIELD_SHARECARE_TOKEN)
    public SharecareToken sharecareToken;

    // [endregion]


    // [region] constructors

    static
    {
        // initialize class variables
        Context applicationContext =
                HealthApplication.getReference();
        _preferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext);
        _encryptionHelper =
                new EncryptionHelper("arKoi?76L=Ea:o,{GmsULuGZuzdE=UJc",
                        "lEMd]jfT%Fp9ZGCe?,cXU4azI($=*hLQ");
        tokenFile = new File(applicationContext.getFilesDir(), TOKEN_FILE);
    }

    private SettingsManager()
    {

    }

    // [endregion]


    // [region] public methods

    public void unfreeze()
    {
        drawerLearned = _preferences.getBoolean(SETTING_DRAWER_USED, false);
    }

    public void freeze()
    {
    }

    public void reset()
    {
        _preferences.edit().clear().commit();
    }


    public static Date getPkcEndpointLastFetched()
    {
        return getDate(PREF_PKC_ENDPOINT_FETCHED);
    }

    public static void setPkcEndpointLastFetched(Date date)
    {
        setDate(PREF_PKC_ENDPOINT_FETCHED, date);
    }

    public static String getPkcEndpointUrl()
    {
        return getString(PREF_PKC_ENDPOINT_URL);
    }

    public static void setPkcEndpointUrl(String url)
    {
        setString(PREF_PKC_ENDPOINT_URL, url);
    }

    // [endregion]


    // [region] helper methods

    public static final boolean getBoolean(String key, boolean value)
    {
        return _preferences.getBoolean(key, value);
    }

    public static final void setBoolean(String key, boolean value)
    {
        // set value
        Editor preferencesEditor = _preferences.edit();
        preferencesEditor.putBoolean(key, value);

        // commit change
        preferencesEditor.commit();
    }

    public static final long getLong(String key, long value)
    {
        return _preferences.getLong(key, value);
    }

    public static final void setLong(String key, long value)
    {
        // set value
        SharedPreferences.Editor preferencesEditor = _preferences.edit();
        preferencesEditor.putLong(key, value);

        // commit change
        preferencesEditor.commit();
    }

    public static final int getInt(String key, int value)
    {
        return _preferences.getInt(key, value);
    }

    public static final void setInt(String key, int value)
    {
        // set value
        Editor preferencesEditor = _preferences.edit();
        preferencesEditor.putInt(key, value);

        // commit change
        preferencesEditor.commit();
    }

    public static final Date getDate(String key)
    {
        // return null if not set
        long timestamp = _preferences.getLong(key, -1);
        if (timestamp < 0)
        {
            return null;
        }

        // otherwise, return date
        return new Date(timestamp);
    }

    public static final void setDate(String key, Date value)
    {
        // clear if not set
        Editor preferencesEditor = _preferences.edit();
        if (value == null)
        {
            preferencesEditor.remove(key);
        }

        // or set date
        else
        {
            preferencesEditor.putLong(key, value.getTime());
        }

        // commit change
        preferencesEditor.commit();
    }

    public static final String getString(String key)
    {
        return getString(key, null);
    }

    public static final String getString(String key, String defaultValue)
    {
        return _preferences.getString(key, defaultValue);
    }

    public static final void setString(String key, String value)
    {
        // clear if value is null
        SharedPreferences.Editor preferencesEditor = _preferences.edit();
        if (value == null)
        {
            preferencesEditor.remove(key);
        }

        // or set value
        else
        {
            // save encrypted value
            preferencesEditor.putString(key, value);
        }

        // commit changes
        preferencesEditor.commit();
    }

    public static final String getStringEncrypted(String key)
    {
        return getStringEncrypted(key, null);
    }

    public static final String getStringEncrypted(String key, String defaultValue)
    {
        String valueEncrypted = _preferences.getString(key, null);
        return valueEncrypted == null ? defaultValue : _encryptionHelper
                .decrypt(valueEncrypted);
    }

    public static final void setStringEncrypted(String key, String value)
    {
        // clear if value is null
        SharedPreferences.Editor preferencesEditor = _preferences.edit();
        if (value == null)
        {
            preferencesEditor.remove(key);
        }

        // or set value
        else
        {
            // encrypt value
            String valueEncrypted = _encryptionHelper.encrypt(value);

            // save encrypted value
            preferencesEditor.putString(key, valueEncrypted);
        }

        // commit changes
        preferencesEditor.commit();
    }

    public boolean authenticateSharecareToken()
    {
        if (!tokenFile.exists())
        {
            return false;
        }

        StringBuilder token = new StringBuilder();
        try
        {
            fin = new FileInputStream(tokenFile);

            int c;
            while ((c = fin.read()) != -1)
            {
                token.append(Character.toString((char)c));
            }
        }
        catch (IOException e)
        {
//            Crashlytics.logException(e);
        }
        finally
        {
            try
            {
                if (fin != null)
                    fin.close();
            }
            catch (IOException e)
            {
//                Crashlytics.logException(e);
            }
        }

        if (StringHelper.isNullOrEmpty(token.toString()))
        {
            return false;
        }
        else
        {
            Gson gson = new Gson();
            return SharecareClient.getSharedInstance().reHydrateSharecareToken(
                    gson.fromJson(_encryptionHelper.decrypt(token.toString()),
                            JsonElement.class));
        }
    }

    // [region] overridden methods

    @Override
    protected void onObservableChanged(ObservableField field, String fieldName,
                                       Object oldValue, Object newValue)
    {
        // prepare for editing
        SharedPreferences.Editor editor = _preferences.edit();

        // handle field settings
        switch (field.value())
        {
            case FIELD_DRAWER_USED:
                editor.putBoolean(SETTING_DRAWER_USED, (Boolean)newValue);
                // finalize editing
                editor.apply();
                break;

            case FIELD_SHARECARE_TOKEN:
                Gson gson = new Gson();

                try
                {
                    if (!tokenFile.exists())
                    {
                        // If no token to set, break
                        if (newValue == null)
                        {
                            break;
                        }
                        tokenFile.createNewFile();
                    }

                    fout = new FileOutputStream(tokenFile);
                    // Clear contents before writing new token
                    fout.write((new String()).getBytes());

                    // If no token to set, break
                    if (newValue == null)
                    {
                        break;
                    }
                    fout.write(_encryptionHelper.encrypt(gson.toJson(newValue))
                            .getBytes());
                }
                catch (IOException e)
                {
//                    Crashlytics.logException(e);
                }
                finally
                {
                    try
                    {
                        if (fout != null)
                            fout.close();
                    }
                    catch (IOException e)
                    {
//                        Crashlytics.logException(e);
                    }
                }
                break;
            // log error for unexpected field settings
            default:
//                Crashlytics.log("SettingsManager Unexpected settings field: "
//                        + fieldName);
                break;
        }
    }

    // [endregion]

}