package nich.work.aequorea.data;

import nich.work.aequorea.common.model.BaseModel;

public class SimpleArticleListModel extends BaseModel {
    private int mId;
    private String mKey;
    private String mTitle;
    
    public int getId() {
        return mId;
    }
    
    public void setId(int id) {
        this.mId = id;
    }
    
    public String getKey() {
        return mKey;
    }
    
    public void setKey(String key) {
        this.mKey = key;
    }
    
    public String getTitle() {
        return mTitle;
    }
    
    public void setTitle(String title) {
        this.mTitle = title;
    }
}
