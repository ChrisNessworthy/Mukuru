package com.mukuru.currencyexchange.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ApiHelper {

    public static StringRequest buildApiStringCall(int method, String URL, final Map<String, String> postParams, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        return new StringRequest(method, urlEncodeParams(URL, postParams),
                listener,
                errorListener
        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> callHeaders = super.getHeaders();
                callHeaders = getHeaderList(callHeaders);
                return callHeaders;
            }
        };
    }

    public static String urlEncodeParams(String url, Map<String, String> params){
        if(params != null){
            if(!url.contains("?")){
                url += "?";
            }else{
                url += "&";
            }
            for(Map.Entry<String, String> entry: params.entrySet()){
                url += entry.getKey() + "=" +entry.getValue() + "&";
            }
            url = trimURL(url);
        }
        return url;
    }

    private static String trimURL(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1)=='&') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    public static Map<String, String> getHeaderList(Map<String, String> headers){
        Map<String, String> headerList = new HashMap<String, String>();

        headerList.putAll(headers);

        return headerList;
    }
}
