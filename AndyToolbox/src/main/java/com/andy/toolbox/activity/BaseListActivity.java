package com.andy.toolbox.activity;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void initView() {
        super.initView();
        mDefaultEmptyView = new DefaultEmptyView(this);
        mDefaultErrorView = new DefaultErrorView(this);
        mDefaultErrorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    /**
     * 是否自动加载数据:默认true
     *
     * @return
     */
    protected boolean isAutoLoadData() {
        return true;
    }

    @Override
    protected void initPageLogic() {
        initRecyclerView();
        initRefreshLayout();
        if (isAutoLoadData()) {
            refresh();
        }
    }

    protected void initRecyclerView() {
        mAdapter = getAdapter();
        mRecyclerView = getRecyclerView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    protected void initRefreshLayout() {
        mRefreshLayout = getSmartRefreshLayout();
        if (isSupportRefresh()) {
            mRefreshLayout.setEnableLoadMore(false);
            mRefreshLayout.setEnableAutoLoadMore(false);//这里需要先做加载更多屏蔽
            mRefreshLayout.setEnableLoadMore(false);
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setOnRefreshListener(refreshlayout -> {
                mRecyclerView.scrollToPosition(0);
                requestData(false);
            });
        }
    }

    /**
     * 是否支持刷新
     *
     * @return
     */
    private boolean isSupportRefresh() {
        return mRefreshLayout != null;
    }

    private void refresh() {
        if (isSupportRefresh()) {
            mRefreshLayout.autoRefresh();
        } else {
            requestData(true);
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return null;
    }

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
    }

    protected abstract Observable<List<T>> getRequestObservable();

    protected abstract BaseQuickAdapter<T, BaseViewHolder> getAdapter();

    protected abstract RecyclerView getRecyclerView();

    protected abstract SmartRefreshLayout getSmartRefreshLayout();

    protected void requestData(boolean showDialog) {
        if (getRequestObservable() == null) {
            mRefreshLayout.finishRefresh();
            if (mCustomEmptyView != null) {
                mAdapter.setEmptyView(mCustomEmptyView);
            } else {
                mAdapter.setEmptyView(mDefaultEmptyView);
            }
            onGetNextStart(null);
        } else {
            getRequestObservable()
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseRxObservable<List<T>>(this, showDialog) {
                        @Override
                        public void onNext(List<T> tPageBean) {
                            super.onNext(tPageBean);
                            onGetNextStart(tPageBean);
                            disposeData(tPageBean);
                            onGetNextEnd(tPageBean);
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            onGetErrorStart(e);
                            disposeError(e);
                            onGetErrorEnd(e);
                        }

                        @Override
                        public void onNetError() {
                            super.onNetError();
                            onGetNetErrorEnd();
                        }
                    });
        }
    }

    protected void disposeError(Throwable e) {
        mRefreshLayout.finishRefresh();
        if (mCustomNetErrorView != null) {
            mAdapter.setEmptyView(mCustomNetErrorView);
        } else {
            mAdapter.setEmptyView(mDefaultErrorView);
        }
    }

    protected void disposeData(List<T> list) {
        mAdapter.replaceData(list);
        mRefreshLayout.finishRefresh();
        if (mCustomEmptyView != null) {
            mAdapter.setEmptyView(mCustomEmptyView);
        } else {
            mAdapter.setEmptyView(mDefaultEmptyView);
        }
        if (getItemDecoration() != null && mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView.addItemDecoration(getItemDecoration());
        }
    }

    protected void onGetNextStart(List<T> pageBean) {

    }

    protected void onGetNextEnd(List<T> pageBean) {

    }

    protected void onGetErrorStart(Throwable e) {

    }

    protected void onGetErrorEnd(Throwable e) {

    }

    protected void onGetNetErrorEnd() {

    }

    protected void resetApiParams(HashMap<String, String> apiParams) {
        mApiParams.clear();
        if (apiParams != null) {
            mApiParams.putAll(apiParams);
        }
    }

    public HashMap<String, String> getApiParams() {
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
