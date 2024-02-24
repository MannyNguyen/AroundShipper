package vn.nip.aroundshipper.Helper;


import vn.nip.aroundshipper.Class.CmmFunc;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by viminh on 10/17/2016.
 */

public class HttpHelper {
    //region POST

    public static String post(String url, List<Map.Entry<String, String>> params) {
        String retValue = "";

        String uri = url;
        if(params != null){
            for (Map.Entry<String, String> param : params) {
                if (!uri.contains("?")) {
                    uri += "?" + param.getKey() + "=" + param.getValue();
                } else {
                    uri += "&" + param.getKey() + "=" + param.getValue();
                }

            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            return null;
        }
        return retValue;
    }

    public static String post(String url, List<Map.Entry<String, String>> params, String jsonData) {
        String retValue = "";

        String uri = url;
        if(params != null){
            for (Map.Entry<String, String> param : params) {
                if (!uri.contains("?")) {
                    uri += "?" + param.getKey() + "=" + param.getValue();
                } else {
                    uri += "&" + param.getKey() + "=" + param.getValue();
                }

            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        List nameValuePairs = new ArrayList();

        if(jsonData!=null){
            nameValuePairs.add(new BasicNameValuePair("data", jsonData));
        }


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            return null;
        }
        return retValue;
    }

    public static String post(String url, List<Map.Entry<String, String>> params, boolean isData) {
        String retValue = "";

        String uri = url;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        List nameValuePairs = new ArrayList();
        if(params != null){
            for (Map.Entry<String, String> param : params) {
                nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));

            }
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            return null;
        }
        return retValue;
    }
    public static String post(String url, List<Map.Entry<String, String>> params,String keyData, String jsonData) {
        String retValue = "";

        String uri = url;
        if(params != null){
            for (Map.Entry<String, String> param : params) {
                if (!uri.contains("?")) {
                    uri += "?" + param.getKey() + "=" + param.getValue();
                } else {
                    uri += "&" + param.getKey() + "=" + param.getValue();
                }

            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);


        List nameValuePairs = new ArrayList();


        if(jsonData!=null){
            nameValuePairs.add(new BasicNameValuePair(keyData, jsonData));
        }


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            return null;
        }
        return retValue;
    }

    public static String post(String url, List<Map.Entry<String, String>> params, List nameValuePairs) {
        String retValue = null;
        try {

            String uri = url;
            if(params != null) {

                for (Map.Entry<String, String> param : params) {
                    if (!uri.contains("?")) {
                        uri += "?" + param.getKey() + "=" + param.getValue();
                    } else {
                        uri += "&" + param.getKey() + "=" + param.getValue();
                    }
                }
            }
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            if (nameValuePairs != null) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                retValue = EntityUtils.toString(response.getEntity());
            }
        }
        catch (Exception e){
            CmmFunc.showError("HttpHelper","POST", e.getMessage());
        }
        return retValue;


    }
    //endregion
    //region GET
    public static String get(String url, List<Map.Entry<String, String>> params) {
        String retValue = "";
        String uri = url;
        if(params != null){
            for (Map.Entry<String, String> param : params) {
                if (!uri.contains("?")) {
                    uri += "?" + param.getKey() + "=" + param.getValue();
                } else {
                    uri += "&" + param.getKey() + "=" + param.getValue();
                }

            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return null;
        }
        return retValue;
    }
//endregion
}