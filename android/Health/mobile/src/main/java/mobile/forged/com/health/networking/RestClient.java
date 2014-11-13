//package mobile.forged.com.health.networking;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
//* Created by visitor15 on 11/11/14.
//*/
//public class RestClient {
//
//    private HashMap<String, Object> body;
//
//    private String bodyJson = "";
//
//    public static enum RequestMethod {
//        GET,
//        POST
//    }
//
//    private ArrayList<NameValuePair> params;
//    private ArrayList <NameValuePair> headers;
//
//    private String url;
//
//    private int responseCode;
//    private String message;
//
//    private String response;
//
//    public String getResponse() {
//        return response;
//    }
//
//    public String getErrorMessage() {
//        return message;
//    }
//
//    public int getResponseCode() {
//        return responseCode;
//    }
//
//    public RestClient() {
//        this.url = "";
//    }
//
//    public RestClient(String url)
//    {
//        this.url = url;
//        params = new ArrayList<NameValuePair>();
//        headers = new ArrayList<NameValuePair>();
//    }
//
//    public void AddParam(String name, String value)
//    {
//        params.add(new BasicNameValuePair(name, value));
//    }
//
//    public void AddHeader(String name, String value)
//    {
//        headers.add(new BasicNameValuePair(name, value));
//    }
//
//    public void executeRequest(RequestMethod method, String url, ArrayList<NameValuePair> params, ArrayList<NameValuePair> headers, HashMap<String, Object> body, String username, String password) {
//        this.params = params;
//        this.headers = headers;
//        this.body = body;
//        this.url = url;
//
//        final Gson gson = new GsonBuilder().create();
//        bodyJson = gson.toJson(body);
//    }
//
//    public void Execute(RequestMethod method) throws Exception
//    {
//        switch(method) {
//            case GET:
//            {
//                //add parameters
//                String combinedParams = "";
//                if(!params.isEmpty()){
//                    combinedParams += "?";
//                    for(NameValuePair p : params)
//                    {
//                        String paramString = p.getName() + "=" + p.getValue();
//                        if(combinedParams.length() > 1)
//                        {
//                            combinedParams  +=  "&" + paramString;
//                        }
//                        else
//                        {
//                            combinedParams += paramString;
//                        }
//                    }
//                }
//
//                HttpGet request = new HttpGet(url + combinedParams);
//
//                //add headers
//                for(NameValuePair h : headers)
//                {
//                    request.addHeader(h.getName(), h.getValue());
//                }
//                executeRequest(request, url);
//                break;
//            }
//
//            case POST:
//            {
//
//                // Create the request body as a MultiValueMap
//                MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
//
//                body.add("field", "value");
//
//// Note the body object as first parameter!
//                HttpEntity httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
//                httpEntity
//
//                HttpPost request = new HttpPost(url);
//
//                //add headers
//                for(NameValuePair h : headers)
//                {
//                    request.addHeader(h.getName(), h.getValue());
//                }
//
//                if(!params.isEmpty()){
//                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//                }
//
//                executeRequest(request, url);
//                break;
//            }
//        }
//    }
//
//    private void executeRequest(HttpUriRequest request, String url)
//    {
//        HttpClient client = new DefaultHttpClient();
//
//        HttpResponse httpResponse;
//
//        try {
//            httpResponse = client.execute(request);
//            httpResponse = client.execute(request);
//            responseCode = httpResponse.getStatusLine().getStatusCode();
//            message = httpResponse.getStatusLine().getReasonPhrase();
//
//            HttpEntity entity = httpResponse.getEntity();
//
//            if (entity != null) {
//                InputStream instream = entity.getContent();
//                response = convertStreamToString(instream);
//
//                // Closing the input stream will trigger connection release
//                instream.close();
//            }
//        }
//        catch (ClientProtocolException e)  {
//            client.getConnectionManager().shutdown();
//            e.printStackTrace();
//        } catch (IOException e) {
//            client.getConnectionManager().shutdown();
//            e.printStackTrace();
//        }
//    }
//
//    private static String convertStreamToString(InputStream is) {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//
//        String line = null;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return sb.toString();
//    }
//}