package nich.work.aequorea.common.utils;

import java.util.Iterator;
import java.util.List;

import nich.work.aequorea.common.Constants;
import nich.work.aequorea.model.entity.Datum;
import nich.work.aequorea.model.entity.search.Content;
import nich.work.aequorea.model.entity.search.SearchDatum;

public class FilterUtils {
    public static boolean underSupport(String type) {
        return !((Constants.ARTICLE_TYPE_THEME.equals(type)) || (Constants.ARTICLE_TYPE_MAGAZINE.equals(type)) || (Constants.ARTICLE_TYPE_MAGAZINE_V2.equals(type)));
    }
    
    public static List<Datum> filterData(List<Datum> data) {
        Iterator<Datum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Datum d = iterator.next();
            if (!FilterUtils.underSupport(d.getArticleType()) || !FilterUtils.underSupport(d.getType())) {
                iterator.remove();
            }
        }
        return data;
    }
    
    public static List<SearchDatum> filterSearchData(List<SearchDatum> data) {
        Iterator<SearchDatum> iterator = data.iterator();
        
        while (iterator.hasNext()) {
            Content content = iterator.next().getContent();
            if (!FilterUtils.underSupport(content.getArticleType())) {
                iterator.remove();
            }
        }
        return data;
    }
}
