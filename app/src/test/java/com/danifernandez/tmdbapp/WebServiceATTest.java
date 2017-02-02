package com.danifernandez.tmdbapp;

import android.content.Context;
import android.view.View;

import com.danifernandez.tmdbapp.ui.MainActivity;
import com.danifernandez.tmdbapp.utils.Constants;
import com.danifernandez.tmdbapp.utils.MyLog;
import com.danifernandez.tmdbapp.ws.WebService;
import com.danifernandez.tmdbapp.ws.WebServiceAT;
import com.danifernandez.tmdbapp.ws.WebServiceCompleteListener;
import com.danifernandez.tmdbapp.ws.WebServiceResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceATTest {

    @Mock
    Context mockContext;

    @Test
    public void response_isCorrect() throws Exception {

        int page = -1;
        WebServiceAT wsAT = new WebServiceAT(mockContext,
                new WebServiceOnCompleteListener(),
                Constants.WS_URL_MOVIES_POPULAR,
                WebService.RequestMethod.GET);
        try {
            wsAT.addName("popularMovies");
            wsAT.addHeader("Content-Type", "application/json;charset=utf-8");
            wsAT.addParam("api_key", Constants.API_KEY);
            wsAT.addParam("sort_by", "popularity.desc");
            if(page != -1){
                wsAT.addParam("page", String.valueOf(page));
            }
            wsAT.execute();
        } catch (Exception e) {
            MyLog.e("WebServiceAT", "Error!", e);
        }
    }

    public class WebServiceOnCompleteListener implements WebServiceCompleteListener {

        @Override
        public void onTaskComplete(WebServiceResult result) {
            // process response of the Web Service
            int statusCode = result.getStatus();
            assertEquals("response OK", 200, statusCode);
        }
    }
}