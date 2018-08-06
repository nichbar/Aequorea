package nich.work.aequorea.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import nich.work.aequorea.R
import nich.work.aequorea.common.view.BaseActivity
import nich.work.aequorea.view.home.HomeFragment
import javax.inject.Inject

class MainActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) startFragment(HomeFragment())
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.view_placeholder, fragment).commitAllowingStateLoss()
    }

    override fun provideContentView(): View {
        return inflate(R.layout.activity_shell)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

}