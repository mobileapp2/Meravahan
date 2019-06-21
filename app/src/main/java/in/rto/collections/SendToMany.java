package in.rto.collections;

import android.app.Application;
import android.content.Context;

public class SendToMany extends Application {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
