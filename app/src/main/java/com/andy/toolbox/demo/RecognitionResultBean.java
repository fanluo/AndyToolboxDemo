package com.andy.toolbox.demo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by luofan on 2019/3/20.
 */
public class RecognitionResultBean {

    @SerializedName("Message")
    private String message;

    @SerializedName("Status")
    private int Status;

    private String status = "OK";

    @SerializedName("Result")
    private List<Data> Result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        this.Status = status;
    }

    public List<Data> getResult() {
        return Result;
    }

    public void setResult(List<Data> result) {
        Result = result;
    }

    public static class Data {

        @SerializedName("Name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
