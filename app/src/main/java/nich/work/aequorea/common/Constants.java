package nich.work.aequorea.common;

public class Constants {
    public static String ARTICLE_ID = "article_id";
    public static String AUTHOR_ID = "author_id";

    // Article Type
    public static final String ARTICLE_TYPE_MAGAZINE = "magazine_article"; // 封面故事
    public static final String ARTICLE_TYPE_MAGAZINE_V2 = "magazine"; // 封面故事
    public static final String ARTICLE_TYPE_NORMAL = "normal_article"; // 一般文章
    public static final String ARTICLE_TYPE_NORMAL_V2 = "normal"; // 一般文章
    public static final String ARTICLE_TYPE_THEME = "theme"; // 专题
    public static final String ARTICLE_TYPE_TOP_ARTICLE = "top_article"; // 头条故事
    public static final String ARTICLE_TYPE_SUBJECT = "subject"; // 单行本


    public static final int TYPE_MAGAZINE = 1;
    public static final int TYPE_NORMAL = 2;
    public static final int TYPE_THEME = 3;
    public static final int TYPE_TOP_ARTICLE = 4;
    public static final int TYPE_SUBJECT = 5;
    
    // Theme
    public static final String THEME = "theme";
    public static final String THEME_LIGHT = "theme_light";
    public static final String THEME_DARK = "theme_dark";
    
    // Trigger
    public static final int AUTO_LOAD_TRIGGER = 30;
}
