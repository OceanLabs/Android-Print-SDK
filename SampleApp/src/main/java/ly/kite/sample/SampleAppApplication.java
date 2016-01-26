package ly.kite.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by dbotha on 23/12/2015.
 */
public class SampleAppApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }

}
