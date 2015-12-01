package com.example.videoplay.bean;

/**
 * 视频信息
 */
public class VideoInfo {

    public VideoInfo(String name, long size, int width, int height, String mimeType, long duration, String path) {
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
        this.mimeType = mimeType;
        this.duration = duration;
        this.path = path;
    }

    public String name;
    public long size;
    public int width;
    public int height;
    public String mimeType;
    public long duration;
    public String path;

}
