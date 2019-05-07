package com.persiandesigners.hyperalborz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;


public class MainActivity extends Activity implements SurfaceHolder.Callback{
    private MediaPlayer mp = null;
    SurfaceView mSurfaceView=null;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(getResources().getBoolean(R.bool.display_gif_as_fpage)){
		    gif();
            ImageView fpage=(ImageView)findViewById(R.id.fpageimg);
            fpage.setVisibility(View.GONE);
        }
		
		final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // TODO: Your application init goes here.
                Intent mInHome = new Intent(MainActivity.this, FistActiivty.class);
                MainActivity.this.startActivity(mInHome);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                MainActivity.this.finish();
            }
        }, 2500);


        SharedPreferences settings = getSharedPreferences("shinaweb4", MODE_PRIVATE);
        if (settings.getBoolean("first", true)) {
            ShortcutIcon();
            SharedPreferences.Editor pref = settings.edit();
            pref.putBoolean("first", false);
            pref.commit();
        }
	}

    private void gif() {
        mp = new MediaPlayer();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceView.setVisibility(View.VISIBLE);
        mSurfaceView.getHolder().addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Uri video=null;

            video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.loading);
            mp.setDataSource(this,video);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get the dimensions of the video
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video
        //be sure to cast these as floats otherwise the calculation will likely be 0
        lp.height = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth);

        //Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);

        //Start video
        mp.setDisplay(holder);
        mp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void ShortcutIcon(){

        try {
            Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            addIntent.putExtra("duplicate", false);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(addIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
