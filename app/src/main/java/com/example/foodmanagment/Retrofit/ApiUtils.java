package com.example.foodmanagment.Retrofit;


public class ApiUtils {



//    public static final String BASE_URL = "https://dev.baskt.com/invoke-lambda/";
public static final String BASE_URL = "https://mytestservices.xyz/restaurant_managment_system_web/";
public static final String BASE_URL_IMAGE = "http://mytestservices.xyz/restaurant_managment_system_web/public/";

    public static APIService getAPIService( boolean isToken) {
        return RetrofitClient.getClient(BASE_URL , isToken).create(APIService.class);
    }

}
