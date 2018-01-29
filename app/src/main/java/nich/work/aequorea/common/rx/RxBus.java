package nich.work.aequorea.common.rx;

import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    private final Subject<Object> mBus;

    private RxBus() {
        mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance() {
        return RxBusHolder.RX_BUS_INSTANCE;
    }

    public void post(int type) {
        post(type, null);
    }

    public void post(int type, @Nullable Object data) {
        mBus.onNext(new RxEvent<>(type, data));
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

    private static class RxBusHolder {
        private static final RxBus RX_BUS_INSTANCE = new RxBus();
    }
}
