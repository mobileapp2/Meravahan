package in.rto.collections.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import in.rto.collections.R;
import in.rto.collections.utilities.UserSessionManager;

public class SplashScreen_Activity extends AppCompatActivity {

    private Context context;
    private ImageView imv_slash;
    private int secondsDelayed = 1;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        init();
    }

    private void init() {
        context = SplashScreen_Activity.this;
        session = new UserSessionManager(context);
        //imv_slash = findViewById(R.id.imv_slash);
        //  final Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom);
//        imv_slash.startAnimation(zoomAnimation);
        final String type = getIntent().getStringExtra("type");

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (session.isUserLoggedIn()) {
                    if (type != null) {
                        Intent i = new Intent(context, Notification_Activity.class);
                        startActivity(i);
                    } else {
                        startActivity(new Intent(context, MainDrawer_Activity.class));
                    }

                } else {
                    startActivity(new Intent(context, Login_Activity.class));
                }
                finish();
            }
        }, secondsDelayed * 2000);
    }
}
