package com.andy.toolbox.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by luofan on 2019/3/20.
 */
public class AndyResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private static final String SUCCESS_CODE = "OK";
    private Type type;

    AndyResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, Type type) {
        this.gson = gson;
        this.adapter = adapter;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return handleNetResult(value.string());
        }/*catch (Exception e){
            throw e;
        }*/ finally {
            value.close();
        }
    }

    private T handleNetResult(String result) throws IOException {
        try {
            Log.e("xxxxxxxxxxx", "xxxxxxxxxxresult=" + result);
            return adapter.read(gson.newJsonReader(new InputStreamReader(new ByteArrayInputStream(result.getBytes()))));
        } catch (Exception e) {
            throw new IOException("数据解析错误");
        }
    }
}
