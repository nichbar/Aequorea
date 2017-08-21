package nich.work.aequorea.common.rx;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    private static RxBus mInstance;
    private final Subject<Object> mBus;
    
    private RxBus() {
        mBus = PublishSubject.create().toSerialized();
    }
    
    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) mInstance = new RxBus();
            }
        }
        return mInstance;
    }
    
    public void post(Object o) {
        mBus.onNext(o);
    }
    
    public void post(int type, Object obj) {
        mBus.onNext(new RxEvent(type, obj));
    }
    
    public Observable<Object> toObservable() {
        return mBus;
    }
    
    public <T> Observable<T> toObservable(Class<T> type) {
        return mBus.ofType(type);
    }
    
    public <T> Observable<T> toObservable(final int type, final Class<T> clazz) {
        return mBus.ofType(RxEvent.class).filter(new Predicate<RxEvent>() {
            @Override
            public boolean test(@NonNull RxEvent event) throws Exception {
                return event.getType() == type;
            }
        }).cast(clazz).observeOn(AndroidSchedulers.mainThread());
    }
}
