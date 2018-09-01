package nich.work.aequorea.view

import android.support.v4.app.Fragment
import android.view.View
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import nich.work.aequorea.R
import nich.work.aequorea.common.view.BaseActivity
import javax.inject.Inject

open class ShellActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun provideContentView(): View {
        return inflate(R.layout.activity_shell)
    }

    fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.view_placeholder, fragment).commitAllowingStateLoss()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

}