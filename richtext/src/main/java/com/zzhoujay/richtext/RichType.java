package com.zzhoujay.richtext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by zhou on 16-7-27.
 * 富文本类型
 */
@SuppressWarnings("ALL")
@IntDef({RichType.HTML, RichType.MARKDOWN})
@Retention(RetentionPolicy.SOURCE)
public @interface RichType {
    int HTML = 0;
    int MARKDOWN = 1;
}
