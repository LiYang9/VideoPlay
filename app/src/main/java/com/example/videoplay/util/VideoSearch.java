package com.example.videoplay.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import com.example.videoplay.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过系统数据库，查找视频，常用
 */
public class VideoSearch {
    private Context mContext;

    private List<VideoInfo> mVideoLists;

    public VideoSearch(Context context) {
        mContext = context;
        mVideoLists = new ArrayList<>();
        search();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void search() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor c = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        c.moveToFirst();
        VideoInfo info;
        while (c.moveToNext()) {
            info = new VideoInfo(c.getString(c.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                    c.getLong(c.getColumnIndex(MediaStore.Video.Media.SIZE)),
                    c.getInt(c.getColumnIndex(MediaStore.Video.Media.WIDTH)),
                    c.getInt(c.getColumnIndex(MediaStore.Video.Media.HEIGHT)),
                    c.getString(c.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)),
                    c.getLong(c.getColumnIndex(MediaStore.Video.Media.DURATION)),
                    c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA)));
            mVideoLists.add(info);
        }
        c.close();

    }

    /**
     * 转换文件大小
     */
//    public String formatFileSize(long fileSize) {
//        DecimalFormat df = new DecimalFormat("#.00");
//        String fileSizeString = "";
//        if (fileSize < 1024) {
//            fileSizeString = df.format((double) fileSize) + "B";
//        } else if (fileSize < 1048576) {
//            fileSizeString = df.format((double) fileSize / 1024) + "K";
//        } else if (fileSize < 1073741824) {
//            fileSizeString = df.format((double) fileSize / 1048576) + "M";
//        } else {
//            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
//        }
//        return fileSizeString;
//    }
    public List<VideoInfo> getmVideoLists() {
        return mVideoLists;
    }
}
