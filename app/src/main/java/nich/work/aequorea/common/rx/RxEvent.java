package nich.work.aequorea.common.rx;

public class RxEvent {
    public static final int EVENT_TYPE_CHANGE_THEME = 1;
    
    private int mCode;
    private Object mObject;
    
    public RxEvent(int type, Object obj){
        mCode = type;
        mObject = obj;
    }
    
    public int getType() {
        return mCode;
    }
    
    public void setType(int mCode) {
        this.mCode = mCode;
    }
    
    public Object getObject() {
        return mObject;
    }
    
    public void setObject(Object mObject) {
        this.mObject = mObject;
    }
}
