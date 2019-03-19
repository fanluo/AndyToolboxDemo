package com.andy.toolbox.activity;

import android.view.View;

import com.andy.toolbox.bean.PageBean;
import com.andy.toolbox.rx.BaseRxObservable;
import com.andy.toolbox.view.DefaultEmptyView;
import com.andy.toolbox.view.DefaultErrorView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.trello.rxlifecycle3.android.ActivityEvent;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 分页列表activity
 * Created by luofan on 2019/3/2.
 */
public abstract class BasePageListActivity<T> extends BaseActivity {

    private RecyclerView mRecyclerView;

    private SmartRefreshLayout mRefreshLayout;

    private static final int DEFAULT_PAGE_NUM = 1;

    protected int mPageSize = 10;

    protected int mCurrentPage = DEFAULT_PAGE_NUM;

    private HashMap<String, String> mApiParams = new HashMap<>();

    private BaseQuickAdapter<T, BaseViewHolder> mAdapter;

    private PageBean<T> mResultPageBean;

    private View mCustomEmptyView;

    private View mCustomNetErrorView;

    private DefaultEmptyView mDefaultEmptyView;

    private DefaultErrorView mDefaultErrorView;
    private String mKeyOfPageSize = "ps";
    private String mKeyOfPageNum = "pn";
    //TODO 后续优化该字段
    private boolean loadMoreAble = false;//是否可以加载更多的数据 需要根据最后一次请求的数据进行比较\

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

    public void setKeyOfPageSize(String pageSizeKey) {
        mKeyOfPageSize = pageSizeKey;
    }

    public void setKeyOfPageNum(String pageNumKey) {
        mKeyOfPageNum = pageNumKey;
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
                requestData(1, false);
            });
            mRefreshLayout.setOnLoadMoreListener(refreshlayout -> {
                //这里需要根据是否能够加载更多来判定
                if (loadMoreAble) {
                    if (mResultPageBean != null) {
                        mCurrentPage = mResultPageBean.getPageNumber() + 1;
                        requestData(mCurrentPage, false);
                    }
                }
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
            requestData(1, true);
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return null;
    }

    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
    }

    protected abstract Observable<PageBean<T>> getRequestObservable();

    protected abstract BaseQuickAdapter<T, BaseViewHolder> getAdapter();

    protected abstract RecyclerView getRecyclerView();

    protected abstract SmartRefreshLayout getSmartRefreshLayout();

    protected void requestData(int pageNum, boolean showDialog) {
        mApiParams.put(mKeyOfPageNum, String.valueOf(pageNum));
        mApiParams.put(mKeyOfPageSize, String.valueOf(mPageSize));
        if (pageNum == DEFAULT_PAGE_NUM) {
            mRefreshLayout.setEnableAutoLoadMore(false);
            mRefreshLayout.setEnableLoadMore(false);
            mRefreshLayout.setNoMoreData(false);
        }
        if (getRequestObservable() == null) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
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
                    .subscribe(new BaseRxObservable<PageBean<T>>(this, showDialog) {
                        @Override
                        public void onNext(PageBean<T> tPageBean) {
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
        if (mCurrentPage == DEFAULT_PAGE_NUM) {
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
        if (mCustomEmptyView != null) {
            mAdapter.setEmptyView(mCustomNetErrorView);
        } else {
            mAdapter.setEmptyView(mDefaultErrorView);
        }
    }

    protected void disposeData(PageBean<T> pageBean) {
        mResultPageBean = pageBean;
        if (mCurrentPage == DEFAULT_PAGE_NUM) {
            mAdapter.replaceData(mResultPageBean.getDataList());
            mRefreshLayout.finishRefresh();
            if (mResultPageBean.getDataList().size() > 0) {
                mRefreshLayout.setEnableLoadMore(true);
                mRefreshLayout.setEnableAutoLoadMore(true);
            }
        } else {
            mAdapter.addData(mResultPageBean.getDataList());
            mRefreshLayout.finishLoadMore(true);
        }
        loadMoreAble = (mResultPageBean.getDataList().size() >= mPageSize);
        if (!loadMoreAble) {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        if (mCustomEmptyView != null) {
            mAdapter.setEmptyView(mCustomEmptyView);
        } else {
            mAdapter.setEmptyView(mDefaultEmptyView);
        }
        if (getItemDecoration() != null && mRecyclerView.getItemDecorationCount() == 0) {
            mRecyclerView.addItemDecoration(getItemDecoration());
        }
    }

    protected void onGetNextStart(PageBean<T> pageBean) {

    }

    protected void onGetNextEnd(PageBean<T> pageBean) {

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

    public void setPageSize(int pageSize) {
        mPageSize = pageSize;
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
