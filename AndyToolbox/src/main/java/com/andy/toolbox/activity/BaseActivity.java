package com.andy.toolbox.activity;

import android.content.Intent;
import android.os.Bundle;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;


/**
 * Created by luofan on 2019/3/2.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
        // 在 super.onCreate(savedInstanceState) 之前调用该方法
        parseIntentData(getIntent());
        super.onCreate(savedInstanceState);
        setContentView(getRootLayoutResId());
        initView();
        initViewListener();
        initPageLogic();
    }

    protected void parseIntentData(Intent intent) {

    }

    protected void initView() {

    }

    protected void initViewListener() {

    }

    protected abstract void initPageLogic();

    protected abstract int getRootLayoutResId();
}
