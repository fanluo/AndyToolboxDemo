package com.andy.toolbox.demo;

import android.view.View;
import android.widget.Toast;

import com.andy.toolbox.activity.BaseListActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;

public class MainActivity extends BaseListActivity<String> {

    RecyclerView mRecyclerView;

    SmartRefreshLayout mRefreshLayout;

    private ItemAdapter mItemAdapter;

    @Override
    protected void initView() {
        super.initView();
        mRecyclerView = findViewById(R.id.recycler_view);
        mRefreshLayout = findViewById(R.id.refresh_layout);
    }

    @Override
    protected void initPageLogic() {
        super.initPageLogic();
    }

    @Override
    protected Observable<List<String>> getRequestObservable() {
        return null;
    }

    @Override
    protected void onGetNextEnd(List<String> tPageBean) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("item" + i);
        }
        mItemAdapter.replaceData(list);
    }

    @Override
    protected BaseQuickAdapter<String, BaseViewHolder> genAdapter() {
        mItemAdapter = new ItemAdapter();
        mItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "onItemClick", Toast.LENGTH_SHORT).show();
            }
        });
        mItemAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "setOnItemChildClickListener", Toast.LENGTH_SHORT).show();
            }
        });
        return mItemAdapter;
    }

    @Override
    protected RecyclerView genRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected SmartRefreshLayout genSmartRefreshLayout() {
        return mRefreshLayout;
    }

    @Override
    protected int getRootLayoutResId() {
        return R.layout.activity_main;
    }
}
