package com.sri.bhai.Helper;



import com.sri.bhai.Service.APIService;
import com.sri.bhai.Service.RetrofitClient;

public class Common {

    public static String topicName = "News";


    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static APIService getFCMService(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }





}
