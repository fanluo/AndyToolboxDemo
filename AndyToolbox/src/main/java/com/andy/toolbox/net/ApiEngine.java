package com.andy.toolbox.net;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by luofan on 2019/3/21.
 */
public class ApiEngine {

    public static boolean DEBUG = true;

    private static final String BASE_URL = "http://www.baidu.com";

    private static final ApiEngine ourInstance = new ApiEngine();

    public static ApiEngine getInstance() {
        return ourInstance;
    }

    private Retrofit mRetrofit;

    private OkHttpClient mOkHttpClient;

    private ApiEngine() {
        mOkHttpClient = genOkHttpClient();
        mRetrofit = gentRetrofit(mOkHttpClient);
    }

    private Retrofit gentRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(AndyConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    private OkHttpClient genOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(15, TimeUnit.SECONDS);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        if (DEBUG) {
            //设置拦截器
            builder.addInterceptor(new LogInterceptor());
        }
        return builder.build();
    }

    //TODO 后续优化
//    public <T> T createByProxy(Class<T> tClass, InvocationHandler invocationHandler) {
//        T t = mRetrofit.create(tClass);
//        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, new ProxyHandler(t));
//    }
//
//    public <T> T get(Class<T> tClass) {
//        T t = mRetrofit.create(tClass);
//        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, new ProxyHandler(t));
//    }

    public <T> T create(Class<T> tClass) {
        return mRetrofit.create(tClass);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
