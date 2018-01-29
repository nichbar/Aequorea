package nich.work.aequorea.common.presenter;

import android.content.Context;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import nich.work.aequorea.common.rx.RxBus;
import nich.work.aequorea.common.rx.RxEvent;
import nich.work.aequorea.common.ui.view.BaseView;

public abstract class BasePresenter<T extends BaseView> {
    protected T mBaseView;
    protected CompositeDisposable mComposite;
    
    public void attach(T view) {
        mBaseView = view;
        mComposite = new CompositeDisposable();
        addSubscription(RxEvent.EVENT_TYPE_CHANGE_THEME, new Consumer<RxEvent>() {
            @Override
            public void accept(RxEvent rxEvent) throws Exception {
                mBaseView.onThemeSwitch();
            }
        });
        
        onAttach();
    }
    
    protected void onAttach() {
        // do nothing
    }
    
    protected void addSubscription(int type, Consumer<RxEvent> consumer) {
        mComposite.add(RxBus.getInstance()
            .toObservable(type, RxEvent.class)
            .subscribe(consumer, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            }));
    }
    
    public void detach() {
        mComposite.clear();
        mBaseView = null;
    }
    
    public String getString(int id) {
        if (mBaseView instanceof Context) {
            return ((Context) mBaseView).getString(id);
        }
        return "";
    }
}
