package com.andy.toolbox.demo.net;

import com.andy.toolbox.demo.RecognitionResultBean;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by luofan on 2019/3/21.
 */
public interface ApiTest {

    String APP_CODE = "dbb4c5c5111342af815bfb54e74ced4e";

    String BASE_URL = "https://plantgw.nongbangzhu.cn";

    String url_recognize_plant = "https://plantgw.nongbangzhu.cn/plant/recognize2";

    String HEAD = "Authorization:APPCODE " + APP_CODE;

    @Headers("Authorization:APPCODE dbb4c5c5111342af815bfb54e74ced4e")
    @FormUrlEncoded
    @POST("/plant/recognize2")
    Observable<String> recognizePlant(@Url String url, @FieldMap HashMap<String, String> apiParams);

    @Headers("Authorization:APPCODE dbb4c5c5111342af815bfb54e74ced4e")
    @FormUrlEncoded
    @POST("/plant/recognize2")
    Observable<RecognitionResultBean> recognizePlant(@FieldMap HashMap<String, String> apiParams);

}
