package foodie.app.rubikkube.foodie;

import android.app.Application;
import android.content.ContextWrapper;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pixplicity.easyprefs.library.Prefs;

public class AppClass extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

    }
}
