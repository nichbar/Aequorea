package nich.work.aequorea.common.presenters;

import nich.work.aequorea.common.ui.activities.BaseActivity;

public interface IPresenter {
    void initialize();

    void attach(BaseActivity activity);

    void detach();
}
