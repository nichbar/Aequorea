package nich.work.aequorea.common.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import butterknife.ButterKnife

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(provideContentView())

        ButterKnife.bind(this)
    }

    protected fun inflate(id: Int): View {
        return layoutInflater.inflate(id, null)
    }

    protected abstract fun provideContentView(): View

}