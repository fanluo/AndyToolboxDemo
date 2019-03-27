package com.andy.toolbox.demo.net;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by luofan on 2019/3/26.
 */
public interface FastApi {
    //TODO 目前有问题，后续修改
    @FormUrlEncoded
    @POST()
    Observable<String> makePostRequest(@Url String url, @FieldMap HashMap<String, String> apiParams);

}
