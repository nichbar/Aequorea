package com.zzhoujay.richtext;

import androidx.annotation.ColorInt;

/**
 * Created by zhou on 2016/11/17.
 * LinkHolder
 */
@SuppressWarnings("unused")
public class LinkHolder {

    private final String url;
    @ColorInt
    private int color;
    private boolean underLine;

    public LinkHolder(String url, int linkColor) {
        this.url = url;
        this.color = linkColor;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    public void setUnderLine(boolean underLine) {
        this.underLine = underLine;
    }

    public String getUrl() {
        return url;
    }
}
