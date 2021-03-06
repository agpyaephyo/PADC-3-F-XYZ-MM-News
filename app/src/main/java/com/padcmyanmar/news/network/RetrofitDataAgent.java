package com.padcmyanmar.news.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.padcmyanmar.news.events.LoadedNewsEvent;
import com.padcmyanmar.news.events.SuccessLoginEvent;
import com.padcmyanmar.news.network.responses.GetNewsResponse;
import com.padcmyanmar.news.network.responses.LoginResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aung on 1/6/18.
 */

public class RetrofitDataAgent implements NewsDataAgent {

    private static RetrofitDataAgent sObjInstance;

    private NewsApi mNewsApi;

    private RetrofitDataAgent() {
        OkHttpClient httpClient = new OkHttpClient.Builder() //1.
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder() //2.
                .baseUrl("http://padcmyanmar.com/padc-3/mm-news/apis/")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(httpClient)
                .build();

        mNewsApi = retrofit.create(NewsApi.class);
    }

    public static RetrofitDataAgent getObjInstance() {
        if (sObjInstance == null)
            sObjInstance = new RetrofitDataAgent();

        return sObjInstance;
    }

    @Override
    public void loadNews() {
        Call<GetNewsResponse> getNewsResponseCall = mNewsApi.loadNews(1, "b002c7e1a528b7cb460933fc2875e916");
        getNewsResponseCall.enqueue(new Callback<GetNewsResponse>() {
            @Override
            public void onResponse(Call<GetNewsResponse> call, Response<GetNewsResponse> response) {
                GetNewsResponse getNewsResponse = response.body();
                if (getNewsResponse != null) {
                    LoadedNewsEvent event = new LoadedNewsEvent(getNewsResponse.getMmNews());
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<GetNewsResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void loginUser(final Context context, String phoneNo, String password) {
        Call<LoginResponse> loginCall = mNewsApi.loginUser(phoneNo, password);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(loginResponse != null) {
                    SuccessLoginEvent event = new SuccessLoginEvent(loginResponse.getLoginUser(), context);
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
        Log.d("", "test log");
    }
}
