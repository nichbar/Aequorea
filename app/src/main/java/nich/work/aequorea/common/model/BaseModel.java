package nich.work.aequorea.common.model;

public class BaseModel {
    private boolean mIsLoading;
    private boolean mIsRefreshing;
    private boolean mIsOver;
    
    public boolean isLoading() {
        return mIsLoading;
    }
    
    public void setLoading(boolean mIsLoading) {
        this.mIsLoading = mIsLoading;
    }
    
    public boolean isRefreshing() {
        return mIsRefreshing;
    }
    
    public void setRefreshing(boolean mIsRefreshing) {
        this.mIsRefreshing = mIsRefreshing;
    }
    
    public boolean isOver() {
        return mIsOver;
    }
    
    public void setOver(boolean mIsOver) {
        this.mIsOver = mIsOver;
    }
}
