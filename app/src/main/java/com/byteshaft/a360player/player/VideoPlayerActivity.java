package com.byteshaft.a360player.player;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.byteshaft.a360player.R;
import com.byteshaft.a360player.utils.AppGlobals;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoPlayerActivity extends MD360PlayerActivity {

    private MediaPlayerWrapper mMediaPlayerWrapper = new MediaPlayerWrapper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaPlayerWrapper.init();
        mMediaPlayerWrapper.setPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                cancelBusy();
            }
        });

        Uri uri = getUri();
        if (uri != null){
            mMediaPlayerWrapper.openRemoteFile(uri.toString());
            mMediaPlayerWrapper.prepare();
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppGlobals.sVideoPaused) {
                    AppGlobals.sPausedByHand = false;
                    mMediaPlayerWrapper.onResume();
                    imageButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    AppGlobals.sPausedByHand = true;
                    mMediaPlayerWrapper.onPause();
                    imageButton.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });
    }

    @Override
    protected MDVRLibrary createVRLibrary() {
        return MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .video(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        mMediaPlayerWrapper.getPlayer().setSurface(surface);
                    }
                })
                .ifNotSupport(new MDVRLibrary.INotSupportCallback() {
                    @Override
                    public void onNotSupport(int mode) {
                        String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                        Toast.makeText(VideoPlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
                    }
                })
                .pinchEnabled(true)
                .gesture(new MDVRLibrary.IGestureListener() {
                    @Override
                    public void onClick(MotionEvent e) {
                        MD360PlayerActivity.getInstance().toggleButtons();
                        if (!AppGlobals.isBufferring) {
                            if (mMediaPlayerWrapper.getPlayer().isPlaying()) {
                                mMediaPlayerWrapper.onPause();
                                imageButton.setImageResource(android.R.drawable.ic_media_play);
                            } else {
                                mMediaPlayerWrapper.onResume();
                                imageButton.setImageResource(android.R.drawable.ic_media_pause);
                            }
                        }
                    }
                })
                .build(R.id.surface_view1,R.id.surface_view2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayerWrapper.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayerWrapper.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayerWrapper.onResume();
    }
}
