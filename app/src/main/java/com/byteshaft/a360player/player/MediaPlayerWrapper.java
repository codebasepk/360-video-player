package com.byteshaft.a360player.player;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import com.byteshaft.a360player.utils.AppGlobals;

import java.io.IOException;
import java.io.InputStream;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;


public class MediaPlayerWrapper implements IMediaPlayer.OnPreparedListener {
    protected IMediaPlayer mPlayer;
    private IjkMediaPlayer.OnPreparedListener mPreparedListener;
    private static final int STATUS_IDLE = 0;
    private static final int STATUS_PREPARING = 1;
    private static final int STATUS_PREPARED = 2;
    private static final int STATUS_STARTED = 3;
    private static final int STATUS_PAUSED = 4;
    private static final int STATUS_STOPPED = 5;
    private int mStatus = STATUS_IDLE;
    private int mBufferPercentage = 0;

    public void init(){
        mStatus = STATUS_IDLE;
        mPlayer = new IjkMediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setWakeMode(AppGlobals.getContext(), PowerManager.ACQUIRE_CAUSES_WAKEUP);
        mPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                Log.i("TAG", "" + percent);
                if (percent > mBufferPercentage) {
                    mBufferPercentage = percent;
                    MD360PlayerActivity.sBufferUpdate.setText(mBufferPercentage + "%");
                }
            }
        });
        mPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                Log.i("IMediaPlayer", "" + what);
                Log.i("IMediaPlayer","" + extra);
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (MD360PlayerActivity.sProgressBar != null) {
                            MD360PlayerActivity.getInstance().disableSensorWhileBuffering();
                            MD360PlayerActivity.sProgressBar.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if (MD360PlayerActivity.sProgressBar != null) {
                            MD360PlayerActivity.getInstance().enableSensorAfterBuffering();
                            MD360PlayerActivity.sProgressBar.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;
            }
        });

        enableHardwareDecoding();
    }

    private void enableHardwareDecoding(){
        if (mPlayer instanceof IjkMediaPlayer){
            IjkMediaPlayer player = (IjkMediaPlayer) mPlayer;
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        }
    }

    public void openRemoteFile(String url){
        try {
            //"http://vod.moredoo.com/u/7575/m3u8/854x480/25883d97c738b1be48d1e106ede2789c/25883d97c738b1be48d1e106ede2789c.m3u8"
            mPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openAssetFile(Context context, String assetPath) {
        try {
            AssetManager am = context.getResources().getAssets();
            final InputStream is = am.open(assetPath);
            mPlayer.setDataSource(new IMediaDataSource() {
                @Override
                public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
                    return is.read(buffer, offset, size);
                }

                @Override
                public long getSize() throws IOException {
                    return is.available();
                }

                @Override
                public void close() throws IOException {
                    is.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IMediaPlayer getPlayer() {
        return mPlayer;
    }

    public void prepare() {
        if (mPlayer == null) return;
        if (mStatus == STATUS_IDLE || mStatus == STATUS_STOPPED){
            mPlayer.prepareAsync();
            mStatus = STATUS_PREPARING;
        }
    }

    public void stop(){
        if (mPlayer == null) return;
        if (mStatus == STATUS_STARTED || mStatus ==  STATUS_PAUSED){
            mPlayer.stop();
            mStatus = STATUS_STOPPED;
        }
    }

    private void pause(){
        if (mPlayer == null) return;
        if (mPlayer.isPlaying() && mStatus == STATUS_STARTED) {
            mPlayer.pause();
            mStatus = STATUS_PAUSED;
        }
    }

    private void start(){
        if (mPlayer == null) return;
        if (mStatus == STATUS_PREPARED || mStatus == STATUS_PAUSED){
            mPlayer.start();
            mStatus = STATUS_STARTED;
        }

    }

    public void setPreparedListener(IMediaPlayer.OnPreparedListener mPreparedListener) {
        this.mPreparedListener = mPreparedListener;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mStatus = STATUS_PREPARED;
        start();
        if (mPreparedListener != null) mPreparedListener.onPrepared(mp);
    }

    public void onPause() {
        pause();
        AppGlobals.sVideoPaused = true;
    }

    public void onResume() {
        start();
        AppGlobals.sVideoPaused = false;
    }

    public void onDestroy() {
        stop();
        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = null;
    }
}
