package nich.work.aequorea.common.rx;

public class RxEvent<T> {
    public static final int EVENT_TYPE_CHANGE_THEME = 1;

    private int mCode;
    private T mData;

    public RxEvent(int type, T data) {
        mCode = type;
        mData = data;
    }

    public int getType() {
        return mCode;
    }

    public void setType(int mCode) {
        this.mCode = mCode;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }
}
