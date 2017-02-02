package com.danifernandez.tmdbapp.utils;

import android.content.Context;

import com.danifernandez.tmdbapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONdecoder {

    private static final String TAG = "JSONdecoder";

    public static Movie decodeResult(JSONObject movieJSON, Context context){
        Movie movie = null;
        try {
            int id = movieJSON.getInt("id");
            String title = movieJSON.getString("title");
            String releaseDate = movieJSON.getString("release_date");
            String year;
            if(releaseDate != null && !releaseDate.isEmpty() && !releaseDate.equalsIgnoreCase("")) {
                year = releaseDate.substring(0, 4);
            }
            else{
                year = context.getResources().getString(R.string.not_available);
            }
            String overviewJSON = movieJSON.getString("overview");
            String overview;
            if(overviewJSON != null && !overviewJSON.isEmpty() && !overviewJSON.equalsIgnoreCase("")) {
                overview = overviewJSON;
            }
            else{
                overview = context.getResources().getString(R.string.not_available);
            }
            String posterPath = null;
            if(!movieJSON.isNull("poster_path")){
                posterPath = movieJSON.getString("poster_path");
            }
            movie = new Movie(id, title, year, overview, posterPath);
            MyLog.d(TAG,movie.toString());
        } catch (JSONException e) {
            MyLog.e(TAG, "Parsing JSON Error!", e);
        }
        return movie;
    }

    public static String decode401Response(String response){
        String errorMsg = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            errorMsg = jsonObject.getString("status_message");
        } catch (JSONException e) {
            MyLog.e(TAG, "Parsing JSON Error!", e);
        }
        return errorMsg;
    }
}
