package nich.work.aequorea.model;

import nich.work.aequorea.common.model.BaseModel;

/**
 * Created by nich on 2017/8/9.
 */
public class AuthorModel extends BaseModel {
    private int mAuthorId;
    
    public int getAuthorId() {
        return mAuthorId;
    }
    
    public void setAuthorId(int authorId) {
        this.mAuthorId = authorId;
    }
}
