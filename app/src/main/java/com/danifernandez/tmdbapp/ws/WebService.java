package com.danifernandez.tmdbapp.ws;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WebService {

    private String TAG = getClass().getSimpleName();

    private boolean debug = true;

    private String url;
    private ArrayList<KeyValue> headers;
    private ArrayList<KeyValue> params;
    private JSONObject json;

    private int responseCode;
    private String responseMessage;
    private String response;

    public enum RequestMethod {
    	GET,
    	POST,
    	PUT,
    	DELETE
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getResponse() {
        return response;
    }
    

    public WebService(String url) {
        this.url = url;
        params = new ArrayList<>();
        headers = new ArrayList<>();
    }

    public void addHeader(String name, String value) {
        if(debug){
            Log.d(TAG,"reqHeader: ( " + name + " , " + value + " )");
        }
        headers.add(new KeyValue(name, value));
    }

    public void addParam(String name, String value) {
        if(debug){
            Log.d(TAG,"reqParam: ( " + name + " , " + value + " )");
        }
        params.add(new KeyValue(name, value));
    }

    public void addJSON(JSONObject object) {
        json = object;
        if(debug){
        	Log.d(TAG,"reqJSON: " + json);
        }
    }

    public void execute(RequestMethod method) throws Exception {

        if(method.equals(RequestMethod.GET) || method.equals(RequestMethod.DELETE)){
            //add parameters
            String combinedParams = "";
            if(!params.isEmpty()){
                combinedParams += "?";
                for(KeyValue p : params)
                {
                    String paramString = p.getKey() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                    if(combinedParams.length() > 1)
                    {
                        combinedParams  +=  "&" + paramString;
                    }
                    else
                    {
                        combinedParams += paramString;
                    }
                }
            }

            URL mURL = new URL(url + combinedParams);
            HttpURLConnection mURLconn = (HttpURLConnection) mURL.openConnection();
            mURLconn.setRequestMethod(method.name());
            mURLconn.setRequestProperty("Content-Length", "0");

            //add headers
            for(KeyValue h : headers) {
                mURLconn.setRequestProperty(h.getKey(), h.getValue());
            }

            mURLconn.setDoInput(true);
            mURLconn.setDoOutput(false);

            executeRequest(mURLconn);
        }
        else if(method.equals(RequestMethod.POST) || method.equals(RequestMethod.PUT)){
            URL mURL = new URL(url);
            HttpURLConnection mURLconn = (HttpURLConnection) mURL.openConnection();
            mURLconn.setRequestMethod(method.name());

            //add headers
            for(KeyValue h : headers) {
                mURLconn.setRequestProperty(h.getKey(), h.getValue());
            }

            mURLconn.setDoInput(true);
            mURLconn.setDoOutput(true);

            if(json != null){
                OutputStreamWriter osw = new OutputStreamWriter(mURLconn.getOutputStream());
                osw.write(json.toString());
                osw.flush();
                osw.close();
            }
            executeRequest(mURLconn);
        }
        else{
            if(debug)   Log.e(TAG,"unknown method");
        }

    }

    private void executeRequest(HttpURLConnection URLconn)
    {
        if(debug){
            Log.d(TAG,"URL: " + url);
        }

        try {
            if(debug)   Log.d(TAG, "Before executing request");
            URLconn.connect();
            if(debug)   Log.d(TAG, "After executing request");
            if(debug){
                Map<String, List<String>> map = URLconn.getHeaderFields();
                if(map!=null) {
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        Log.d(TAG, "respHeader: ( " + entry.getKey() + " , " + entry.getValue() + " )");
                    }
                }
            }
            responseCode = URLconn.getResponseCode();
            responseMessage = URLconn.getResponseMessage();
            if(debug){
                Log.d(TAG,"responseCode: " + responseCode + " , " + responseMessage);
            }

            if(responseCode== HttpURLConnection.HTTP_OK){
                InputStream is = URLconn.getInputStream();
                response = convertStreamToString(is);
                // Closing the input stream will trigger connection release
                is.close();
            }
            else{
                InputStream is = URLconn.getErrorStream();
                response = convertStreamToString(is);
                // Closing the input stream will trigger connection release
                is.close();
            }
            if(debug){
                Log.d(TAG, "response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            URLconn.disconnect();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}