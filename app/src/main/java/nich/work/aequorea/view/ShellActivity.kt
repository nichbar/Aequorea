package nich.work.aequorea.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import nich.work.aequorea.R
import nich.work.aequorea.common.Constants
import nich.work.aequorea.common.view.BaseActivity
import nich.work.aequorea.view.author.AuthorFragment
import javax.inject.Inject

open class ShellActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) handleIntent(intent.extras)
    }

    override fun provideContentView(): View {
        return inflate(R.layout.activity_shell)
    }

    private fun handleIntent(bundle: Bundle?) {
        // Leave the fragment itself to retain data from bundle for surviving configuration changes.
        val fragmentToShow = bundle?.getString(Constants.INTENT_TYPE)

        if (fragmentToShow.isNullOrEmpty()) return

        when(fragmentToShow) {
            AUTHOR -> startFragment(AuthorFragment().withBundle(bundle))
        }
    }

    fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.view_placeholder, fragment).commitAllowingStateLoss()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    companion object {
        const val AUTHOR = "author"
    }

}