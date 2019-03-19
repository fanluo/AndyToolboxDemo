package com.andy.toolbox.demo;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by luofan on 2019/3/16.
 */
public class ItemAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ItemAdapter() {
        super(R.layout.adapter_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_name, item);
        helper.addOnClickListener(R.id.tv_name);
    }
}
