package foodie.app.rubikkube.foodie;

import android.app.Application;
import android.content.ContextWrapper;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.pixplicity.easyprefs.library.Prefs;

import java.net.URISyntaxException;

import foodie.app.rubikkube.foodie.utilities.Constant;
import io.socket.client.IO;
import io.socket.client.Socket;

public class AppClass extends Application {

    private Socket mSocket;

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

        {
            try {
                mSocket = IO.socket(Constant.CHAT_SERVER_URL);
                //mSocket = IO.socket(Prefs.getString("socketUrl",""));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public Socket getSocket() {
        return mSocket;
    }

}
