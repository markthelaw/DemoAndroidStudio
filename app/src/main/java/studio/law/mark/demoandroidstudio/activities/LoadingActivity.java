package studio.law.mark.demoandroidstudio.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import studio.law.mark.demoandroidstudio.Constants;
import studio.law.mark.demoandroidstudio.R;
import studio.law.mark.demoandroidstudio.fragments.ImageGridFragment;

/**
 * Created by GTR on 2/1/2015.
 */


public class LoadingActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                // if not logged in, go to log in screen


//                Intent i = new Intent(LoadingActivity.this,
//                        MainActivity.class);
//                startActivity(i);
                Intent intent = new Intent(LoadingActivity.this, SimpleImageActivity.class);
                intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
                startActivity(intent);




                // close this activity
                overridePendingTransition(R.anim.mainfadein,
                        R.anim.splashfadeout);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
