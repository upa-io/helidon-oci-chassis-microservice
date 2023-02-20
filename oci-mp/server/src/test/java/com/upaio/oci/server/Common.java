package com.upaio.oci.server;

import com.upaio.oci.client.api.ApiException;
import com.upaio.oci.client.api.GreetApi;
import com.upaio.oci.client.model.GreetUpdate;
import com.upaio.oci.client.model.GreetResponse;

/**
 * Common utilities for all server test classes
 */
class Common {
    public static String getDefaultMessage(GreetApi greetApi) throws ApiException  {
        GreetResponse greetResponse = greetApi.getDefaultMessage();
        return greetResponse.getMessage();
    }

    public static String getMessage(GreetApi greetApi, String path) throws ApiException {
        GreetResponse greetResponse = greetApi.getMessage(path);
        return greetResponse.getMessage();
    }

    static void updateGreeting(GreetApi greetApi, String newGreeting) throws ApiException {
        greetApi.updateGreeting(new GreetUpdate().greeting(newGreeting));
    }
}
