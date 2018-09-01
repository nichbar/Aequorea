package nich.work.aequorea.view

import android.os.Bundle
import dagger.android.support.HasSupportFragmentInjector
import nich.work.aequorea.view.home.HomeFragment

class MainActivity : ShellActivity(), HasSupportFragmentInjector {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) startFragment(HomeFragment())
    }

}