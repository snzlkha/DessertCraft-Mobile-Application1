package com.yourapp.desertcraft.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yourapp.desertcraft.utils.Val;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCall {


    private static final Gson gson = new GsonBuilder().setLenient().create();
    private static Retrofit retrofit;

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Val.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }

}
