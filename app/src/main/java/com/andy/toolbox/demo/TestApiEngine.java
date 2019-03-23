package com.andy.toolbox.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andy.toolbox.activity.BaseActivity;
import com.andy.toolbox.demo.net.ApiTest;
import com.andy.toolbox.net.ApiEngine;
import com.andy.toolbox.rx.BaseRxObservable;
import com.google.gson.Gson;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by luofan on 2019/3/21.
 */
public class TestApiEngine extends BaseActivity {


    Button mBtn;

    TextView mTvResult;

    @Override
    protected void initPageLogic() {
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                makeRequest();
                makeRequestString();
            }
        });
    }

    private String getBase64() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_no_data);
        return bitmapToBase64(bitmap);
    }

    public String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void makeRequest() {
        String imgBase64 = getBase64();
        Log.e("xxxxxxxxx", "xxxxxxxxxxxxximgBase64" + imgBase64);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("img_base64", imgBase64);
        ApiEngine.getInstance().create(ApiTest.class).recognizePlantWithUrl(ApiTest.url_recognize_plant, hashMap)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseRxObservable<RecognitionResultBean>(TestApiEngine.this, true) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //为请求提供一个取消的手段
                    }

                    @Override
                    public void onNext(RecognitionResultBean value) {
                        //请求成功
                        Gson gson = new Gson();
                        Log.e("xxxxxxxxxx", "xxxxxxxxxxonNext=" + gson.toJson(value));
                        mTvResult.setText(gson.toJson(value));
                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求出错
                        Log.e("xxxxxxxxxx", "xxxxxxxxxxonError=" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        //请求完成
                    }
                });
    }

    private void makeRequestString() {
        String imgBase64 = getBase64();
        Log.e("xxxxxxxxx", "xxxxxxxxxxxxximgBase64" + imgBase64);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("img_base64", imgBase64);
        ApiEngine.getInstance().create(ApiTest.class).recognizePlantToString(ApiTest.url_recognize_plant, hashMap)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseRxObservable<String>(TestApiEngine.this, true) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //为请求提供一个取消的手段
                    }

                    @Override
                    public void onNext(String value) {
                        //请求成功
                        Gson gson = new Gson();
                        Log.e("xxxxxxxxxx", "xxxxxxxxxxonNext=" + value);
                        mTvResult.setText(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求出错
                        Log.e("xxxxxxxxxx", "xxxxxxxxxxonError=" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        //请求完成
                    }
                });
    }

    @Override
    protected void initView() {
        super.initView();
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mTvResult = findViewById(R.id.tv_result);
    }

    @Override
    protected int getRootLayoutResId() {
        return R.layout.activity_test_apiengine;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
