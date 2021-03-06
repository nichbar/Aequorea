package nich.work.aequorea.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import nich.work.aequorea.common.ui.widget.glide.CircleTransformation;

public class ImageHelper {
    public static Target setImage(Context context, String url, ImageView imageView) {
        return setImage(context, url, imageView, false, -1);
    }
    
    public static Target setImage(Context context, String url, ImageView imageView, boolean isRound) {
        return setImage(context, url, imageView, isRound, -1);
    }
    
    public static Target setImage(Context context, String url, ImageView imageView, int placeHolder) {
        return setImage(context, url, imageView, false, placeHolder);
    }
    
    public static Target setImage(Context context, String url, ImageView imageView, Drawable placeHolder) {
        return setImage(context, url, imageView, false, placeHolder);
    }
    
    public static Target setImage(Context context, String url, ImageView imageView, boolean isRound, int placeHolder) {
        if (isRound) {
            return Glide.with(context)
                .load(url)
                .transform(new CircleTransformation(context))
                .placeholder(placeHolder)
                .into(imageView);
        } else {
            return Glide.with(context)
                .load(url)
                .placeholder(placeHolder)
                .into(imageView);
        }
    }
    
    public static Target setImage(Context context, String url, ImageView imageView, boolean isRound, Drawable placeHolder) {
        if (isRound) {
            return Glide.with(context)
                .load(url)
                .transform(new CircleTransformation(context))
                .placeholder(placeHolder)
                .into(imageView);
        } else {
            return Glide.with(context)
                .load(url)
                .placeholder(placeHolder)
                .into(imageView);
        }
    }
    
    public static Drawable generateRandomPlaceholder(String s) {
        ColorDrawable drawable;
        if (TextUtils.isEmpty(s)) {
            drawable = new ColorDrawable();
            drawable.setColor(Color.rgb(20, 20, 20));
            
            return drawable;
        }
        
        int dividerIndex = s.lastIndexOf("/");
        String tempString = s.substring(dividerIndex, dividerIndex + 5);
        
        int count = 0;
        char a = 'a';
        
        for (int i = 0; i < 5; i++) {
            count = tempString.charAt(i) - a;
        }
        
        int r = Math.abs(count % 255);
        
        drawable = new ColorDrawable();
        drawable.setColor(Color.rgb(r, r, r));
        
        return drawable;
    }
}
