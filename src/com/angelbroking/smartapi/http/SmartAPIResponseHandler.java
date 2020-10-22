package com.angelbroking.smartapi.http;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.DataException;
import com.angelbroking.smartapi.http.exceptions.GeneralException;
import com.angelbroking.smartapi.http.exceptions.InputException;
import com.angelbroking.smartapi.http.exceptions.NetworkException;
import com.angelbroking.smartapi.http.exceptions.OrderException;
import com.angelbroking.smartapi.http.exceptions.PermissionException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.TokenException;

import okhttp3.Response;

/**
 * Response handler for handling all the responses.
 */
public class SmartAPIResponseHandler {

    public JSONObject handle(Response response, String body) throws IOException, SmartAPIException, JSONException {
        if (response.header("Content-Type").contains("json")) {
            JSONObject jsonObject = new JSONObject(body);
            if(jsonObject.has("error_type")) {
                throw dealWithException(jsonObject, response.code());
            }
            return jsonObject;
        } else {
            throw new DataException("Unexpected content type received from server: "+ response.header("Content-Type")+" "+response.body().string(), 502);
        }
    }

    public String handle(Response response, String body, String type) throws IOException, SmartAPIException, JSONException {
        if (response.header("Content-Type").contains("csv")) {
            return body;
        } else if(response.header("Content-Type").contains("json")){
            throw dealWithException(new JSONObject(response.body().string()), response.code());
        } else {
            throw new DataException("Unexpected content type received from server: "+ response.header("Content-Type")+" "+response.body().string(), 502);
        }
    }


    private SmartAPIException dealWithException(JSONObject jsonObject, int code) throws JSONException {
        String exception = jsonObject.getString("error_type");

        switch (exception){
            // if there is a token exception, generate a signal to logout the user.
            case "TokenException":
                if(SmartConnect.sessionExpiryHook != null) {
                	SmartConnect.sessionExpiryHook.sessionExpired();
                }
                return  new TokenException(jsonObject.getString("message"), code);

            case "DataException": return new DataException(jsonObject.getString("message"), code);

            case "GeneralException": return new GeneralException(jsonObject.getString("message"), code);

            case "InputException": return new InputException(jsonObject.getString("message"), code);

            case "OrderException": return new OrderException(jsonObject.getString("message"), code);

            case "NetworkException": return new NetworkException(jsonObject.getString("message"), code);

            case "PermissionException": return new PermissionException(jsonObject.getString("message"), code);

            default: return new SmartAPIException(jsonObject.getString("message"), code);
        }
    }

}
