package com.yanjushop.m.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yanjushop.m.api.RequestApi;
import com.yanjushop.m.entry.API;
import com.yanjushop.m.okhttp.cookie.SimpleCookieJar;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * ProgectName：application
 * Creator：long
 * Date：2016/5/27
 */
public class RetrofitUtils {
    private static RequestApi jsonRequet;
    private static RequestApi stringRequet;
    private RetrofitUtils() {

    }

    public static RequestApi getRetrofitJsonRequet(){
        if (jsonRequet == null) {
            synchronized (RetrofitUtils.class) {
                if (jsonRequet == null) {
                    jsonRequet = new Retrofit.Builder()
                            .baseUrl(API.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(getGson()))
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(getOkHttpClient())
                            .build()
                            .create(RequestApi.class);
                }
            }
        }
        return jsonRequet;
    }

    public static RequestApi getStringRequet(){
        if (stringRequet == null) {
            synchronized (RetrofitUtils.class) {
                if (stringRequet == null) {
                    stringRequet = new Retrofit.Builder()
                            .baseUrl(API.BASE_URL)
                            .client(getOkHttpClient())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build()
                        .create(RequestApi.class);
                }
            }
        }
        return stringRequet;
    }

    private static Gson getGson(){
        return new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }
    private static OkHttpClient getOkHttpClient(){
        return OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .cookieJar(new SimpleCookieJar())
                .addInterceptor(new LoggerInterceptor())
                .build();
    }
}
