package com.contact.unmatch.utils;

import android.content.Context;

import com.contact.unmatch.UnmatchApp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpUtils {

    private static final String BASE_URL = "https://api.unmatchapp.com/";
    public static final String API_URL = BASE_URL + "api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get( String url,  RequestParams params, String token,  AsyncHttpResponseHandler responseHandler) {
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.addHeader("Authorization", "Bearer " + token);
        client.get(url, params, responseHandler);

    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.addHeader("Content-Type", "application/json");
        client.addHeader("Accept", "text/plain");
        client.post(url, params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void post(Context context, String token, String url, JSONObject jsonObject, AsyncHttpResponseHandler responseHandler ) throws UnsupportedEncodingException {
        if(token != null){
            client.addHeader("Authorization", "Bearer " + token);
        }
        StringEntity entity = new StringEntity(jsonObject.toString());
        client.post(context, url, entity, "application/json", responseHandler);
    }

    public static void refreshtoken(RefreshTokenListener tokenListener) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("accessToken", Utils.getAccessToken());
            obj.put("refreshToken", Utils.getRefreshToken());
            String url = API_URL + "Token/RefreshToken";
            try {
                HttpUtils.post(UnmatchApp.getAppContext(), null, url, obj, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if(response.getBoolean("isAuthSuccessful")){
                                String access_token = response.getString("accessToken");
                                String refresh_token = response.getString("refreshToken");

                                Utils.setAccessToken(access_token);
                                Utils.setRefreshToken(refresh_token);
                                tokenListener.onSuccessRefreshToken();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tokenListener.onFailureRefreshToken();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        tokenListener.onFailureRefreshToken();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        tokenListener.onFailureRefreshToken();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        tokenListener.onFailureRefreshToken();
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
