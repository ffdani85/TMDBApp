package com.danifernandez.tmdbapp;

import com.danifernandez.tmdbapp.utils.Constants;
import com.danifernandez.tmdbapp.ws.WebService;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebServiceTest {

    @Test
    public void response_isCorrect() throws Exception {
        int page = -1;

        WebService webService = new WebService(Constants.WS_URL_MOVIES_POPULAR);

        try {
            webService.addHeader("Content-Type", "application/json;charset=utf-8");
            webService.addParam("api_key", Constants.API_KEY);
            webService.addParam("sort_by", "popularity.desc");
            if(page != -1){
                webService.addParam("page", String.valueOf(page));
            }
            webService.execute(WebService.RequestMethod.GET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int statusCode = webService.getResponseCode();
        assertEquals(200, statusCode);
    }
}