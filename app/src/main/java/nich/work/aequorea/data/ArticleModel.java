package nich.work.aequorea.data;

import java.util.List;

import nich.work.aequorea.data.entity.Datum;

public class ArticleModel {
    private long mId;
    private Datum mData;
    private List<Datum> mRecommendDataList;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }
    
    public void setData(Datum article) {
        mData = article;
    }
    
    public Datum getData(){
        return mData;
    }
    
    public List<Datum> getRecommendDataList() {
        return mRecommendDataList;
    }
    
    public void setRecommendDataList(List<Datum> mRecommendDataList) {
        if (this.mRecommendDataList == null) {
            this.mRecommendDataList = mRecommendDataList;
        }
    }
}
