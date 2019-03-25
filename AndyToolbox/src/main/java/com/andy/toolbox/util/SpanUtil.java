package com.andy.toolbox.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

/**
 * Created by luofan on 2018/12/11.
 */
public final class SpanUtil {

    public static SpannableString getMatchTextColorSpan(String content, String keyText, int color) {
        if (TextUtils.isEmpty(content)) {
            return new SpannableString("");
        } else if (TextUtils.isEmpty(keyText)) {
            return new SpannableString(content);
        }
        SpannableString spannableString = new SpannableString(content);
        for (int i = 0; content.indexOf(keyText, i) != -1; i++) {
            int index = content.indexOf(keyText, i);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            int end = index + keyText.length();
            spannableString.setSpan(colorSpan, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}
