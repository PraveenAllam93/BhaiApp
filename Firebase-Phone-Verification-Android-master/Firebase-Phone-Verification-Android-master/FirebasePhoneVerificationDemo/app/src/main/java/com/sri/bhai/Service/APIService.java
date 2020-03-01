package com.sri.bhai.Service;


import com.sri.bhai.model.MyResponse;
import com.sri.bhai.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAArl9O0Qs:APA91bFnOrPLxTcgQMzWQJ2vq3VzOHGVoblPer7g9l4aJiRZ10kvHM7M_J8EbJh7v8zMmtnzpfH7Ch3Tl-5I_qLL6B6SnUguStcKyq-CNr5pSj706wwT8tpb6qEvBy3e78IqzMtvEtlK"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
