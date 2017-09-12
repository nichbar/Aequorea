package nich.work.aequorea.common.utils;

import nich.work.aequorea.common.Constants;

public class FilterUtils {
    public static boolean underSupport(String type) {
        return !(type.equals(Constants.ARTICLE_TYPE_THEME) || type.equals(Constants.ARTICLE_TYPE_MAGAZINE) || type
            .equals(Constants.ARTICLE_TYPE_MAGAZINE_V2));
    }
}
