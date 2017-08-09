package nich.work.aequorea.common.presenter;

import nich.work.aequorea.common.ui.activities.BaseActivity;

public abstract class AbsPresenter implements IPresenter {
    protected BaseActivity mActivity;

    public AbsPresenter(){
        initialize();
    }

    @Override
    public void attach(BaseActivity activity) {
        mActivity = activity;
    }

    @Override
    public void detach() {
        mActivity = null;
    }
}
