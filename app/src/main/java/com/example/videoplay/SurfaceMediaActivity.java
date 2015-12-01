package com.example.videoplay;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

/**
 * 3.使用SurfaceView+MediaPlayer播放
 * <p/>
 * Created by ly on 2015/11/20.
 */
public class SurfaceMediaActivity extends Activity {

    private static final String TAG = "SurfaceMediaActivity";
    private MediaPlayer mPlayer;
    private RelativeLayout mContainer;
    private SurfaceView mSurfaceView;
    private Activity mContext;
    private Handler mHandler = new Handler();
    private MediaController mController;
    private int mCurrentPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setViews();

        if (savedInstanceState != null) {
            mCurrentPos = savedInstanceState.getInt("mCurrentPos");
            Log.i(TAG, "取出了==" + mCurrentPos);
        }
        initSurfaceView();
    }

    /**
     * 直接用代码设置布局，自己写项目时，可以直接拷贝这个类，来进行视频播放，只需要在intent中传值path即可（绝对路径）
     * Intent intent = new Intent(mContext, SurfaceMediaActivity.class);
     * intent.putExtra("path", mListPath.get(mPos));
     * startActivity(intent);
     */
    private void setViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//由重力感应器决定手机方向

        mContainer = new RelativeLayout(mContext);
        mContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mContainer.setBackgroundColor(Color.BLACK);
        mContainer.setGravity(Gravity.CENTER);
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mContainer.addView(mSurfaceView);
        setContentView(mContainer);
    }

    private void initSurfaceView() {
        mSurfaceView.getHolder().addCallback(callBack);
        //需要确保底层表面是一个推送缓冲区表面，需要将其应用于视频播放和摄像头预览
        //mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private SurfaceHolder.Callback callBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surface is created");

            //在创建好之后，播放视频
            openVideo();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "surface is changed:width=" + width + " height=" + height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surface is destroyed");
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        //销毁时记录播放位置
        outState.putInt("mCurrentPos", mPlayer.getCurrentPosition());
        mController.hide();//在旋转时隐藏控制器
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();//释放
            mPlayer = null;
        }
    }

    /**
     * 初始化MediaPlayer
     */
    private void openVideo() {
        if (mPlayer != null) {
            return;
        }

        mPlayer = new MediaPlayer();
        mPlayer.setDisplay(mSurfaceView.getHolder());
        String path = getIntent().getStringExtra("path");
        try {
            mPlayer.setDataSource(path);//此路径必须是绝对路径
            //开始加载，加载好之后才能播放，对应preparedListener
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "该视频不能播放，2秒后自动退出", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContext.finish();
                }
            }, 1500);
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setScreenOnWhilePlaying(true);//播放时屏幕常亮
        mPlayer.setOnCompletionListener(completionListener);//视频结束播放监听
        mPlayer.setOnErrorListener(errorListener);
        mPlayer.setOnPreparedListener(preparedListener);//视频准备监听
        mPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
    }


    private MediaController.MediaPlayerControl mediaPlayControl = new MediaController.MediaPlayerControl() {
        @Override
        public void start() {
            mPlayer.start();
        }

        @Override
        public void pause() {
            mPlayer.pause();
        }

        @Override
        public int getDuration() {
            return mPlayer.getDuration();
        }

        @Override
        public int getCurrentPosition() {
            return mPlayer.getCurrentPosition();
        }

        @Override
        public void seekTo(int pos) {
            mPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return mPlayer.getAudioSessionId();
        }
    };
    /**
     * 播放结束
     */
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "play is over");

            //播放结束重新播放
            mp.seekTo(mCurrentPos = 0);
            mp.start();
        }
    };

    /**
     * 准备播放
     */
    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.i(TAG, "play is ready");

            setVideoSize(mp);

            mPlayer.seekTo(mCurrentPos);
            mPlayer.start();//播放

            mController = new MediaController(mContext);
            mController.setMediaPlayer(mediaPlayControl);
            mController.setAnchorView(mContainer);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mController.setEnabled(true);
                }
            });
        }
    };

    private void setVideoSize(MediaPlayer mp) {

        int width = mp.getVideoWidth();
        int height = mp.getVideoHeight();

        int screenWidth = getScreenWidthAndHeight()[0];
        int screenHeight = getScreenWidthAndHeight()[1];

        int orientation = getResources().getConfiguration().orientation;

        if (width > height) {//如果宽度大于高度，说明横屏合适
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                LayoutParams params = mSurfaceView.getLayoutParams();
                params.width = screenWidth;
                params.height = (int) (screenWidth * height / (width * 1.0));
                mSurfaceView.setLayoutParams(params);
            }
        } else {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LayoutParams params = mSurfaceView.getLayoutParams();
                params.width = (int) (screenHeight * width / (height * 1.0));
                params.height = screenHeight;
                mSurfaceView.setLayoutParams(params);
            }
        }
    }

    /**
     * 获取屏幕宽高
     */
    private int[] getScreenWidthAndHeight() {
        Display display = mContext.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.i(TAG, "dm.widthPixels=" + size.x + " dm.heightPixels=" + size.y);
        return new int[]{size.x, size.y};
    }


    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "play error");
            return false;
        }
    };
    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.i(TAG, "play width=" + width + " height=" + height);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mController != null) {
                if (mController.isShowing()) {
                    mController.hide();
                } else {
                    mController.show(1500);
                }
            }
        }
        return false;
    }
}