package nich.work.aequorea.common.presenter;

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
        addSubscription(RxEvent.EVENT_TYPE_CHANGE_THEME, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                mBaseView.onThemeSwitch();
            }
        });
        
        onAttach();
    }
    
    protected void onAttach() {
    }
    
    protected void addSubscription(int type, Consumer consumer) {
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
}
