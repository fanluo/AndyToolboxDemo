package com.andy.toolbox.rx;

import android.content.Context;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by luofan on 2019/3/6.
 */
public class BaseRxObservable<T> implements Observer<T> {

    private boolean mShowToast;

    private Context mContext;

    public BaseRxObservable(Context context, boolean showToast) {
        mContext = context;
        mShowToast = showToast;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    public void onNetError() {

    }

    @Override
    public void onComplete() {

    }
}
