package nich.work.aequorea.main.model;

import java.util.List;

import nich.work.aequorea.main.model.article.Data;
import nich.work.aequorea.main.model.mainpage.Datum;

public class ArticleModel {
    private long mId;
    private Data mData;
    private List<Datum> mRecommendDataList;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }
    
    public void setData(Data article) {
        mData = article;
    }
    
    public Data getData(){
        return mData;
    }
    
    public List<Datum> getRecommendDataList() {
        return mRecommendDataList;
    }
    
    public void setRecommendDataList(List<Datum> mRecommendDataList) {
        this.mRecommendDataList = mRecommendDataList;
    }
}
