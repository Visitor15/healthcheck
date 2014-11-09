package mobile.forged.com.health.networking;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by visitor15 on 9/22/14.
 */
public class HttpConnector {
    private URL mUrl;

    public HttpConnector() {}

    public String postForResponse(String url) throws Exception {
        HttpURLConnection httpConnection = null;
        String response = "";
        try {
            mUrl = new URL(url);
            httpConnection = (HttpURLConnection) mUrl.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Content-Language", Locale.getDefault().getISO3Language());

            httpConnection.setUseCaches(false);
            httpConnection.setDoInput(true);

            InputStream inStream = httpConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

            String line = "";
            StringBuilder strBuilder = new StringBuilder(line);
            while((line = reader.readLine()) != null) {
                strBuilder.append(line);
            }

            response = strBuilder.toString();
            reader.close();
        } catch (Exception e) {
            throw e;
        }
        finally {
            if(httpConnection != null) {
                httpConnection.disconnect();
            }
        }

        return response;
    }
}
