package com.byteshaft.a360player.player;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.asha.vrlib.MDVRLibrary;
import com.byteshaft.a360player.R;

/**
 * using MD360Renderer
 * <p/>
 * Created by hzqiujiadi on 16/1/22.
 * hzqiujiadi ashqalcn@gmail.com
 */

public abstract class MD360PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton imageButton;
    private FrameLayout frameLayout;
    private static MD360PlayerActivity sInstance;
    private ImageView glassView;


    public static MD360PlayerActivity getInstance() {
        return sInstance;
    }

    public static void startVideo(Context context, Uri uri) {
        start(context, uri, VideoPlayerActivity.class);
    }


    private static void start(Context context, Uri uri, Class<? extends Activity> clz) {
        Intent i = new Intent(context, clz);
        i.setData(uri);
        context.startActivity(i);
    }

    private MDVRLibrary mVRLibrary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view
        setContentView(R.layout.activity_md_multi);

        // init VR Library
        mVRLibrary = createVRLibrary();
        glassView = (ImageView) findViewById(R.id.glass_view);

        // interactive mode switcher
//        final Button interactiveModeSwitcher = (Button) findViewById(R.id.button_interactive_mode_switcher);
//        interactiveModeSwitcher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mVRLibrary.switchInteractiveMode(MD360PlayerActivity.this);
//                updateInteractiveModeText(interactiveModeSwitcher);
//            }
//        });
//        updateInteractiveModeText(interactiveModeSwitcher);

        // display mode switcher
        sInstance = this;
        final ImageButton displayModeSwitcher = (ImageButton) findViewById(R.id.button_display_mode_switcher);
        displayModeSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVRLibrary.switchDisplayMode(MD360PlayerActivity.this);
                updateDisplayModeText(displayModeSwitcher);
            }
        });
        updateDisplayModeText(displayModeSwitcher);

        findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVRLibrary.resetPinch();
                // reset touch
                if (mVRLibrary.getInteractiveMode() == MDVRLibrary.INTERACTIVE_MODE_TOUCH) {
                    mVRLibrary.resetTouch();
                }
            }
        });

        imageButton = (ImageButton) findViewById(R.id.play_pause);
        imageButton.setOnClickListener(this);
//        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
//        linearLayout.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                linearLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
            }
        }, 2000);


    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mVRLibrary.handleTouchEvent(event) || super.onTouchEvent(event);
    }

    private void updateDisplayModeText(ImageButton button) {
        String text = null;
        switch (mVRLibrary.getDisplayMode()) {
            case MDVRLibrary.DISPLAY_MODE_NORMAL:
                button.setImageResource(R.drawable.vr);
                glassView.setVisibility(View.GONE);
                text = "NORMAL";
                break;
            case MDVRLibrary.DISPLAY_MODE_GLASS:
                button.setImageResource(R.drawable.landscape);
                glassView.setVisibility(View.VISIBLE);
                text = "GLASS";
                break;
        }
//        if (!TextUtils.isEmpty(text)) button.setText(text);
    }

    private void updateInteractiveModeText(Button button) {
        String text = null;
        switch (mVRLibrary.getInteractiveMode()) {
            case MDVRLibrary.INTERACTIVE_MODE_MOTION:
                text = "MOTION";
                break;
            case MDVRLibrary.INTERACTIVE_MODE_TOUCH:
                text = "TOUCH";
                break;
        }
        if (!TextUtils.isEmpty(text)) button.setText(text);
    }

    abstract protected MDVRLibrary createVRLibrary();

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
    }

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null) {
            return null;
        }
        return i.getData();
    }

    public void toggleButtons() {
        if (frameLayout.getVisibility() == View.VISIBLE) {
//            linearLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
        } else {
//            linearLayout.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    linearLayout.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.GONE);
                }
            }, 2000);
        }
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    private void exitDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit");
        alertDialog.setMessage("Do you really want to exit");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                    }
                });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_pause:

                break;
        }
    }
}