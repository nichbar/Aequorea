package nich.work.aequorea.common

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

private val UI_EXECUTOR = MainThreadExecutor()

fun runOnIoThread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}

fun runOnUiThread(f: () -> Unit) {
    UI_EXECUTOR.execute(f)
}

private class MainThreadExecutor : Executor {
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mainThreadHandler.post(command)
    }
}
