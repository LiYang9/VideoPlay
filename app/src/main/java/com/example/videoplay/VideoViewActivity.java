package com.example.videoplay;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * 2.使用VideoView播放视频
 * <p/>
 * Created by ly on 2015/11/20.
 */
public class VideoViewActivity extends Activity {
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_view);
        init();
    }

    /*
    MediaController--一个包含媒体播放器(MediaPlayer)控件的视图。包含了一些典型的按钮，像"播放(Play)/暂停(Pause)", "倒带(Rewind)",
    "快进(Fast Forward)"与进度滑动器(progress slider)。
    它管理媒体播放器(MediaController)的状态以保持控件的同步。
    通过编程来实例化使用这个类。这个媒体控制器将创建一个具有默认设置的控件，并把它们放到一个窗口里漂浮在你的应用程序上。
    具体来说，这些控件会漂浮在通过setAnchorView()指定的视图上。
    如果这个窗口空闲3秒那么它将消失，直到用户触摸这个视图的时候重现。
     */
    private void init() {
        VideoView videoView = (VideoView) findViewById(R.id.video_view);

        String path = getIntent().getStringExtra("path");
        videoView.setVideoPath(path);

        MediaController mediaController = new MediaController(mContext);
        mediaController.setAnchorView(videoView);//设置要漂浮显示之上的view
        videoView.setMediaController(mediaController);//
        videoView.start();
    }
}