package com.zzhoujay.richtext;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.TintContextWrapper;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.TextView;

import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.ext.HtmlTagHandler;
import com.zzhoujay.richtext.ext.LongClickableLinkMovementMethod;
import com.zzhoujay.richtext.ig.BitmapPool;
import com.zzhoujay.richtext.parser.CachedSpannedParser;
import com.zzhoujay.richtext.parser.Html2SpannedParser;
import com.zzhoujay.richtext.parser.ImageGetterWrapper;
import com.zzhoujay.richtext.parser.SpannedParser;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-5-28.
 * 富文本生成器
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RichText implements ImageGetterWrapper, ImageLoadNotify {

    public static boolean debugMode = false;
    
    private static final String TAG_TARGET = "target";
    
    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("<(img|IMG)(.*?)>");
    private static Pattern IMAGE_WIDTH_PATTERN = Pattern.compile("(width|WIDTH)=\"(.*?)\"");
    private static Pattern IMAGE_HEIGHT_PATTERN = Pattern.compile("(height|HEIGHT)=\"(.*?)\"");
    private static Pattern IMAGE_SRC_PATTERN = Pattern.compile("(src|SRC)=\"(.*?)\"");
    
    private HashMap<String, ImageHolder> imageHolderMap;
    
    @RichState
    private int state = RichState.ready;
    
    private final SpannedParser spannedParser;
    private final CachedSpannedParser cachedSpannedParser;
    private final SoftReference<TextView> textViewSoftReference;
    private final RichTextConfig config;
    private int count;
    private int loadingCount;
    private SoftReference<SpannableStringBuilder> richText;


    static void bind(Object tag, RichText richText) {
        RichTextPool.getPool().bind(tag, richText);
    }

    /**
     * 清除tag绑定的所有RichText，并清除所有的图片缓存
     *
     * @param tag TAG
     */
    public static void clear(Object tag) {
        RichTextPool.getPool().clear(tag);
    }


    /**
     * 设置缓存目录，若不设置将不会对图片进行本地缓存
     *
     * @param cacheDir 缓存目录，请确保对该目录有读写权限
     */
    public static void initCacheDir(File cacheDir) {
        BitmapPool.setCacheDir(cacheDir);
    }

    /**
     * 清除缓存
     */
    public static void recycle() {
        BitmapPool.getPool().clear();
        RichTextPool.getPool().recycle();
    }

    /**
     * 设置缓存目录，若不设置将不会对图片进行本地缓存
     *
     * @param context Context
     */
    public static void initCacheDir(Context context) {
        File cacheDir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            cacheDir = context.getExternalCacheDir();
        }
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        initCacheDir(cacheDir);
    }

    RichText(RichTextConfig config, TextView textView) {
        this.config = config;
        this.textViewSoftReference = new SoftReference<>(textView);
        this.spannedParser = new Html2SpannedParser(new HtmlTagHandler(textView));
        if (config.clickable > 0) {
            textView.setMovementMethod(new LongClickableLinkMovementMethod());
        } else if (config.clickable == 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        this.cachedSpannedParser = new CachedSpannedParser();
    }

    public static RichTextConfig.RichTextConfigBuild from(String source) {
        return fromHtml(source);
    }

    public static RichTextConfig.RichTextConfigBuild fromHtml(String source) {
        return from(source, RichType.HTML);
    }

    public static RichTextConfig.RichTextConfigBuild fromMarkdown(String source) {
        return from(source, RichType.MARKDOWN);
    }

    public static RichTextConfig.RichTextConfigBuild from(String source, @RichType int richType) {
        return new RichTextConfig.RichTextConfigBuild(source, richType);
    }

    protected void generateAndSet() {
        final TextView textView = textViewSoftReference.get();
        if (textView != null) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    asyncGenerate(textView);
                }
            });
        }
    }

    private void asyncGenerate(TextView textView) {
        AsyncTask<WeakReference<TextView>, Void, CharSequence> asyncTask = new AsyncTask<WeakReference<TextView>, Void, CharSequence>() {

            private WeakReference<TextView> textViewWeakReference;

            @Override
            protected CharSequence doInBackground(WeakReference<TextView>... params) {
                WeakReference<TextView> weakReference = params[0];
                textViewWeakReference = weakReference;
                TextView tv = weakReference.get();
                if (tv == null) {
                    return null;
                } else {
                    return generateRichText();
                }
            }

            @Override
            protected void onPostExecute(CharSequence charSequence) {
                if (textViewWeakReference == null) {
                    return;
                }
                TextView tv = textViewWeakReference.get();
                if (tv == null || charSequence == null) {
                    return;
                }
                tv.setText(charSequence);

                if (config.callback != null) {
                    config.callback.done(false);
                }
            }
        };

        WeakReference<TextView> weakReference = new WeakReference<>(textView);
        // 启动AsyncTask
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || config.singleLoad) {
            asyncTask.execute(weakReference);
        } else {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, weakReference);
        }
    }

    /**
     * 生成富文本
     *
     * @return Spanned
     */
    private CharSequence generateRichText() {
        TextView textView = textViewSoftReference.get();
        if (textView == null) {
            return null;
        }
        if (config.richType != RichType.MARKDOWN) {
            analyzeImages(config.source);
        } else {
            imageHolderMap = new HashMap<>();
        }
        SpannableStringBuilder spannableStringBuilder = null;
        if (config.cacheType > CacheType.NONE) {
            spannableStringBuilder = RichTextPool.getPool().loadCache(config.source);
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = parseRichText();
        }
        richText = new SoftReference<>(spannableStringBuilder);
        config.imageGetter.registerImageLoadNotify(this);
        cachedSpannedParser.setAccentColor(getAccentColor(textView.getContext()));
        count = cachedSpannedParser.parse(spannableStringBuilder, this, config);
        return spannableStringBuilder;
    }

    @NonNull
    private SpannableStringBuilder parseRichText() {
        SpannableStringBuilder spannableStringBuilder;
        state = RichState.loading;
        String source = config.source;

        Spanned spanned = spannedParser.parse(source);
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            if (spanned == null) {
                spanned = new SpannableString("");
            }
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }
        return spannableStringBuilder;
    }

    /**
     * 从文本中拿到<img/>标签,并获取图片url和宽高
     */
    private synchronized void analyzeImages(String text) {
        imageHolderMap = new HashMap<>();
        ImageHolder holder;
        int position = 0;
        Matcher imageTagMatcher = IMAGE_TAG_PATTERN.matcher(text);
        while (imageTagMatcher.find()) {
            String image = imageTagMatcher.group(2).trim();
            Matcher imageSrcMatcher = IMAGE_SRC_PATTERN.matcher(image);
            String src = null;
            if (imageSrcMatcher.find()) {
                src = imageSrcMatcher.group(2).trim();
            }
            if (TextUtils.isEmpty(src)) {
                continue;
            }
            holder = new ImageHolder(src, position, config);
            if (!config.autoFix && !config.resetSize) {
                Matcher imageWidthMatcher = IMAGE_WIDTH_PATTERN.matcher(image);
                if (imageWidthMatcher.find()) {
                    holder.setWidth(parseStringToInteger(imageWidthMatcher.group(2).trim()));
                }
                Matcher imageHeightMatcher = IMAGE_HEIGHT_PATTERN.matcher(image);
                if (imageHeightMatcher.find()) {
                    holder.setHeight(parseStringToInteger(imageHeightMatcher.group(2).trim()));
                }
            }
            imageHolderMap.put(holder.getSource(), holder);
            position++;
        }
    }

    /**
     * 判断Activity是否已经结束
     *
     * @param context context
     * @return true：已结束
     */
    private static boolean activityIsAlive(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof TintContextWrapper) {
            context = ((TintContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return false;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) context).isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int parseStringToInteger(String integerStr) {
        int result = -1;
        if (!TextUtils.isEmpty(integerStr)) {
            try {
                result = Integer.parseInt(integerStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static boolean isGif(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 && "gif".equalsIgnoreCase(path.substring(index + 1));
    }

    /**
     * 回收所有图片和任务
     */
    public void clear() {
        TextView textView = textViewSoftReference.get();
        if (textView != null) {
            textView.setText(null);
        }
        config.imageGetter.recycle();
    }


    /**
     * 获取解析的状态
     *
     * @return state
     * @see RichState
     */
    @RichState
    public int getState() {
        return state;
    }

    @Override
    public Drawable getDrawable(String source) {
        loadingCount++;
        if (config.imageGetter == null) {
            return null;
        }
        if (config.noImage) {
            return null;
        }
        TextView textView = textViewSoftReference.get();
        if (textView == null) {
            return null;
        }
        // 判断activity是否已结束
        if (!activityIsAlive(textView.getContext())) {
            return null;
        }
        ImageHolder holder;
    
        holder = imageHolderMap.get(source);
        if (holder == null) {
            holder = new ImageHolder(source, loadingCount - 1, config);
            imageHolderMap.put(source, holder);
        }
        holder.setImageState(ImageHolder.ImageState.INIT);
        if (config.imageFixCallback != null) {
            config.imageFixCallback.onInit(holder);
            if (!holder.isShow()) {
                return null;
            }
        }
        return config.imageGetter.getDrawable(holder, config, textView);
    }

    @Override
    public void done(Object from) {
        if (from instanceof Integer) {
            int loadedCount = (int) from;
            if (loadedCount >= count) {
                state = RichState.loaded;
                if (config.cacheType >= CacheType.LAYOUT) {
                    SpannableStringBuilder ssb = richText.get();
                    if (ssb != null) {
                        RichTextPool.getPool().cache(config.source, ssb);
                    }
                }
                if (config.callback != null) {
                    TextView textView = textViewSoftReference.get();
                    if (textView != null) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                config.callback.done(true);
                            }
                        });
                    }
                }
            }
        }
    }
    
    public void reload() {
        generateAndSet();
    }
    
    private int getAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

}
