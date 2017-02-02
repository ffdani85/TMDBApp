package com.danifernandez.tmdbapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.danifernandez.tmdbapp.utils.JSONdecoder;
import com.danifernandez.tmdbapp.utils.Movie;
import com.danifernandez.tmdbapp.utils.MyLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class JSONdecoderTest {
    @Test
    public void checkJSONdecoding() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        String resultStr = "{\"poster_path\":\"\\/WLQN5aiQG8wc9SeKwixW7pAR8K.jpg\",\"adult\":false,\"overview\":\"The quiet life of a terrier named Max is upended when his owner takes in Duke, a stray whom Max instantly dislikes.\",\"release_date\":\"2016-06-18\",\"genre_ids\":[12,16,35,10751],\"id\":328111,\"original_title\":\"The Secret Life of Pets\",\"original_language\":\"en\",\"title\":\"The Secret Life of Pets\",\"backdrop_path\":\"\\/lubzBMQLLmG88CLQ4F3TxZr2Q7N.jpg\",\"popularity\":175.700622,\"vote_count\":1983,\"video\":false,\"vote_average\":5.8}";

        JSONObject resultJSON = null;
        try {
            resultJSON = new JSONObject(resultStr);
        } catch (JSONException e) {
            MyLog.e("JSONdecoderTest", "Parsing JSON Error!", e);
        }

        if(resultJSON != null){
            Movie movie = JSONdecoder.decodeResult(resultJSON, appContext);
            assertNotNull(movie);
        }
    }
}
