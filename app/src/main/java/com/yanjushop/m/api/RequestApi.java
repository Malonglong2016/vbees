package com.yanjushop.m.api;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * ProgectName：vbees
 * Creator：long
 * Date：2016/8/15
 */
public interface RequestApi {
    @GET("")
    Observable<String> wechatPay(@Url String url);
}
