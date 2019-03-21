//package com.andy.toolbox.net;
//
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//
//import java.lang.reflect.Method;
//import java.util.Date;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableSource;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Function;
//
///**
// * Created by luofan on 2019/3/21.
// */
//public class DefaultProxyHandler {
//    private Object mProxyObject;
//    private Throwable mRefreshTokenError = null;
//    private static long tokenChangedTime = 0;
//    private static int retryTime = 60;
//    public static boolean retryOk = false;//是否请求重试成功 默认不成功
//    public static boolean startRefreshToken = false;//是否开始重试请求
//    private boolean retryComplete = false;//是否重试完成
//
//    public ProxyHandler(Object proxyObject) {
//        mProxyObject = proxyObject;
//    }
//
//    @Override
//    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
//        return Observable.just("").flatMap(new Function<Object, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(Object o) throws Exception {
//                try {
//                    return (Observable<?>) method.invoke(mProxyObject, args);
//                } catch (Exception e) {
//
//                }
//                return null;
//            }
//        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
//                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                        Long tokenRetryTime = new Date().getTime() - tokenChangedTime;
//                        //&& tokenRetryTime > retryTime
//                        if (throwable instanceof ApiException) {
//                            ApiException apiException = (ApiException) throwable;
//                            String tryCode = NetErrorCode.USER_TOKEN_PAST;
//                            if (AppManager.getInstance().getPlatFormInfo() == PlatFormInfo.PLAT_INFO.FAMILYFARM.platform) {
//                                //苗叔平
//                                tryCode = FamilyNetErrorCode.USER_TOKEN_PAST;
//                            }
//                            if (TextUtils.equals(tryCode, apiException.getCode())) {
//                                //token过期 需要重试
//                                String token = SPUtil.getString(FarmConstants.USER_TOKEN);//用户token
//                                String pwdEncrypedPwd = SPUtil.getString(FarmConstants.USER_ENCRYPED_PWD);
//                                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(pwdEncrypedPwd)) {
//                                    if (!startRefreshToken) {
//                                        startRefreshToken = true;
//                                        retryOk = false;
//                                        return refreshTokenWhenTokenInvalid();
//                                    } else {
//                                        //已经有开始进行处理了 那么就
//                                        while (startRefreshToken) {
//                                            try {
//                                                Thread.sleep(100);
//                                            } catch (Exception e) {
//
//                                            }
//                                        }
//                                        if (retryOk) {
//                                            return Observable.just("");
//                                        } else {
//                                            return Observable.error(throwable);
//                                        }
//                                    }
//                                }
//                            }/*else if(TextUtils.equals(NetErrorCode.USER_TOKEN_KICK,apiException.getCode())){
//                                //重试失败 那么就需要跳转到登陆页面
//                            }*/
//                        }
//                        return Observable.error(throwable);
//                    }
//                });
//            }
//        });
//    }
//
//    class RetryHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    }
//
//    private Observable<?> refreshTokenWhenTokenInvalid() {
//        ApiParams params = new ApiParams();
//        String tokenValue = SPUtil.getString(FarmConstants.USER_TOKEN);
//        String pwdEncrypedPwd = SPUtil.getString(FarmConstants.USER_ENCRYPED_PWD);
//        params.put("token", tokenValue);
//        params.put("deviceId", "" + DeviceUtil.getDeviceId());
//        params.put("encryptedPwd", pwdEncrypedPwd);
//        ApiEngine.getTokenManagerApi().autoLogin(TokenApi.TOKEN_API_URL, params)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new FarmSubscriber<RefreshTokenBean>(AppManager.getApp(), false, false) {
//
//                    @Override
//                    public void onComplete() {
//                        super.onComplete();
//
//                    }
//
//                    @Override
//                    public void onNext(RefreshTokenBean tokenBean) {
//                        retryComplete = true;
//                        tokenChangedTime = new Date().getTime();
//                        SPUtil.putString(FarmConstants.USER_TOKEN, tokenBean.getToken());
//                        mRefreshTokenError = null;
//                        retryOk = true;
//                        PlatFormNotifyBean notifyBean = new PlatFormNotifyBean();
//                        notifyBean.setNotifyType(PlatFormNotifyUtil.NOTIFY_REFRESH_TOKEN_OK);
//                        EventBus.getDefault().post(notifyBean);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        retryComplete = true;
//                        retryOk = false;
//                        mRefreshTokenError = e;
//                    }
//                });
//        while (true) {
//            if (retryComplete) {
//                if (mRefreshTokenError != null) {
//                    Throwable error = new Throwable();
//                    error.initCause(mRefreshTokenError);
//                    mRefreshTokenError = null;
//                    startRefreshToken=false;
//                    return Observable.error(error);
//                } else {
//                    startRefreshToken=false;
//                    return Observable.just("");
//                }
//            }
//            try {
//                Thread.sleep(100);
//            } catch (Exception e) {
//
//            }
//        }
//    }
//}
