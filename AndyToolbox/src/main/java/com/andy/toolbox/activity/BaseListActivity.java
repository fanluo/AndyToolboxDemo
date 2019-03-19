package com.andy.toolbox.activity;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


import com.andy.toolbox.rx.BaseRxObservable;
import com.andy.toolbox.view.DefaultEmptyView;
import com.andy.toolbox.view.DefaultErrorView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 不分页列表activity
 * Created by luofan on 2019/3/2.
 */
public abstract class BaseListActivity<T> extends BaseActivity {

    private RecyclerView mRecyclerView;

    private SmartRefreshLayout mRefreshLayout;

    private HashMap<String, String> mApiParams = new HashMap<>();

    private BaseQuickAdapter<T, BaseViewHolder> mAdapter;

    private View mCustomEmptyView;

    private View mCustomNetErrorView;

    private DefaultEmptyView mDefaultEmptyView;

    private DefaultErrorView mDefaultErrorView;

    private boolean needLazyRequest = true;//是否需要在懒加载的时候进行请求

    @Override
    protected void initView() {
        mDefaultEmptyView = new DefaultEmptyView(this);
        mDefaultErrorView = new DefaultErrorView(this);
    }

    @Override
    protected void initPageLogic() {
        mAdapter = genAdapter();
        mRecyclerView = genRecyclerView();
        mRefreshLayout = genSmartRefreshLayout();
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableAutoLoadMore(false);//这里需要先做加载更多屏蔽
        RecyclerView.LayoutManager layoutManager = getDefaultLayoutManager();
        if (layoutManager == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(refreshlayout -> {
            mRecyclerView.scrollToPosition(0);
            requestData(true);
        });
        if (getLazyRequest()) {
            mRefreshLayout.autoRefresh();
        }
        mDefaultErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshLayout.autoRefresh();
            }
        });
    }

    protected RecyclerView.LayoutManager getDefaultLayoutManager() {
        return null;
    }

    protected abstract Observable<List<T>> getRequestObservable();

    protected abstract BaseQuickAdapter<T, BaseViewHolder> genAdapter();

    protected abstract RecyclerView genRecyclerView();

    protected abstract SmartRefreshLayout genSmartRefreshLayout();

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
    }

    protected void requestData(boolean showToast) {
        if (getRequestObservable() == null) {
            return;
        }
        getRequestObservable()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseRxObservable<List<T>>(this, showToast) {
                    @Override
                    public void onNext(List<T> resultList) {
                        super.onNext(resultList);
                        onGetNextStart(resultList);
                        mAdapter.replaceData(resultList);
                        mRefreshLayout.finishRefresh();
                        if (mCustomEmptyView != null) {
                            mAdapter.setEmptyView(mCustomEmptyView);
                        } else {
                            mAdapter.setEmptyView(mDefaultEmptyView);
                        }
                        if (getItemDecoration() != null && mRecyclerView.getItemDecorationCount() == 0) {
                            mRecyclerView.addItemDecoration(getItemDecoration());
                        }
                        onGetNextEnd(resultList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onGetErrorStart(e);
                        mRefreshLayout.finishRefresh();
                        if (mCustomNetErrorView != null) {
                            mAdapter.setEmptyView(mCustomNetErrorView);
                        } else {
                            mAdapter.setEmptyView(mDefaultErrorView);
                        }
                        onGetErrorEnd(e);
                    }
                });
    }

    protected void onGetNextStart(List<T> tPageBean) {

    }

    protected void onGetNextEnd(List<T> tPageBean) {

    }

    protected void onGetErrorStart(Throwable e) {

    }

    protected void onGetErrorEnd(Throwable e) {

    }

    protected void resetApiParams(HashMap apiParams) {
        mApiParams.clear();
        if (apiParams != null) {
            mApiParams.putAll(apiParams);
        }
    }

    protected boolean getLazyRequest() {
        return needLazyRequest;
    }

    public HashMap<String, String> getRequestParams() {
        return mApiParams;
    }

    public void setCustomEmptyView(@NonNull View emptyView) {
        mCustomEmptyView = emptyView;
    }

    public void setCustomNetErrorView(@NonNull View netErrorView) {
        mCustomNetErrorView = netErrorView;
    }

    public DefaultEmptyView getDefaultEmptyView() {
        return mDefaultEmptyView;
    }

    public DefaultErrorView getDefaultErrorView() {
        return mDefaultErrorView;
    }
}
