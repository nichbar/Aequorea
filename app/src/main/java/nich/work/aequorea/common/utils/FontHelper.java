package nich.work.aequorea.common.utils;

import android.content.res.Resources;

import nich.work.aequorea.Aequorea;
import nich.work.aequorea.R;

public class FontHelper {
    private static int mDefaultSize = -1;
    private static int mDefaultFontSpacing = -1;
    
    private static final String FONT_SIZE = "font_size";
    private static final String FONT_FAMILY = "font_family";
    private static final String FONT_SPACING = "font_spacing";
    
    public static final String SERIF = "serif";
    public static final String MONOSPACE = "monospace";
    public static final String SANS_SERIF = "sans_serif";
    
    private static final int MAX_FONT_SIZE = 24;
    private static final int MIN_FONT_SIZE = 10;
    private static final int MAX_SPACING = 10;
    private static final int MIN_SPACING = 1;
    
    public static int getFontSize() {
        return SPUtils.getInt(FONT_SIZE, getDefaultSize());
    }
    
    private static int getDefaultSize() {
        if (mDefaultSize == -1) {
            Resources r = Aequorea.Companion.getApp().getResources();
            mDefaultSize = (int) (r.getDimension(R.dimen.size_article_content) / r.getDisplayMetrics().density);
        }
        return mDefaultSize;
    }
    
    public static int getFontSpacing() {

        return SPUtils.getInt(FONT_SPACING, getDefaultSpacing());
    }
    
    private static int getDefaultSpacing() {
        if (mDefaultFontSpacing == -1) {
            Resources r = Aequorea.Companion.getApp().getResources();
            mDefaultFontSpacing = (int) (r.getDimension(R.dimen.spacing_article_content) / r.getDisplayMetrics().density);
        }
        return mDefaultFontSpacing;
    }
    
    public static int setDefaultFontSize() {
        SPUtils.setInt(FONT_SIZE, getDefaultSize());
        return getDefaultSize();
    }
    
    public static int setDefaultSpacing() {
        SPUtils.setInt(FONT_SPACING, getDefaultSpacing());
        return getDefaultSpacing();
    }
    
    public static String getFontFamily() {
        return SPUtils.getString(FONT_FAMILY, SANS_SERIF);
    }
    
    public static void setFontFamily(String f){
        SPUtils.setString(FONT_FAMILY, f);
    }
    
    public static int increaseFontSize() {
        int size = getFontSize();
        if (size >= MAX_FONT_SIZE) {
            return size;
        }
        
        size++;
        SPUtils.setInt(FONT_SIZE, size);
        return size;
    }
    
    public static int decreaseFontSize() {
        int size = getFontSize();
        if (size < MIN_FONT_SIZE) {
            return size;
        }
        
        size--;
        SPUtils.setInt(FONT_SIZE, size);
        return size;
    }
    
    public static int expandSpacing() {
        int spacing = getFontSpacing();
        if (spacing >= MAX_SPACING) {
            return spacing;
        }
        
        spacing++;
        SPUtils.setInt(FONT_SPACING, spacing);
        return spacing;
    }
    
    public static int condenseSpacing() {
        int spacing = getFontSpacing();
        if (spacing <= MIN_SPACING) {
            return spacing;
        }
        
        spacing --;
        SPUtils.setInt(FONT_SPACING, spacing);
        return spacing;
    }
}
