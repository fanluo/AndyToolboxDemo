package com.andy.toolbox.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.toolbox.R;


/**
 * 默认错误页面
 * Created by luofan on 2018/4/25.
 */

public class DefaultErrorView extends FrameLayout {

    private ImageView mImageView;

    private TextView mTextView;

    public DefaultErrorView(@NonNull Context context) {
        this(context, null);
    }

    public DefaultErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_default_error_view, this);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.text);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setText(String str) {
        mTextView.setText(str);
    }

    public void setTextResId(int resId) {
        mTextView.setText(resId);
    }

    public void setImageResId(int resId) {
        mImageView.setImageResource(resId);
    }
}
