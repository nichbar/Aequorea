package nich.work.aequorea;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class Aequorea extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
