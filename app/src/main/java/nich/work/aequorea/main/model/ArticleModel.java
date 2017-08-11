package nich.work.aequorea.main.model;

import nich.work.aequorea.main.model.article.Data;

public class ArticleModel {
    private long mId;
    private Data mData;

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
}
