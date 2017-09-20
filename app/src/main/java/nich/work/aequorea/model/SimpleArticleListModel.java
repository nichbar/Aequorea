package nich.work.aequorea.model;

import nich.work.aequorea.common.model.BaseModel;

public class SimpleArticleListModel extends BaseModel {
    private int mId;
    private String mKey;
    
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
}
