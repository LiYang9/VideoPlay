package com.example.videoplay.util;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 查找视频，很耗时
 * Created by ly on 2015/11/20.
 */
public class VideoSearchUtil {

    private List<String> mVideoPaths;//视频路径

    private List<String> mVideoNames;//视频名字

    /**
     * 要判断的视频格式
     */
    private static String[] videoStr = {"avi", "mp4", "wma", "rmvb", "rm",
            "3gp", "flv", "webm"};

    /**
     * @param rootPath 查找的根目录
     */
    public VideoSearchUtil(String rootPath) {
        mVideoPaths = new ArrayList<>();
        mVideoNames = new ArrayList<>();
        searchVideo(rootPath);
    }

    /**
     * 查找path下的视频文件
     */
    private void searchVideo(String path) {
        File rootFile = new File(path);
        File[] files = rootFile.listFiles();
        for (File f : files) {
            String fPath = f.getPath();
            String fName = f.getName();
            if (f.isDirectory()) {//如果是目录，递归向下一层查找
                searchVideo(fPath);
            } else {
                //获取后缀名
                String suffix = getSuffix(fPath);
                //判断是否为视频格式，是的话就添加到集合中
                if (fPath.contains(".") && !TextUtils.isEmpty(suffix)) {
                    for (String s : videoStr) {
                        if (s.equals(suffix)) {
                            mVideoPaths.add(fPath);
                            mVideoNames.add(fName);
                        }
                    }
                }
            }
        }
    }

    /**
     * 通过文件路径获取后缀名
     *
     * @param path 文件路径
     * @return 后缀名
     */
    public String getSuffix(String path) {
        return path.substring(path.lastIndexOf(".") + 1).toLowerCase(Locale.ENGLISH);
    }

    public List<String> getVideoPaths() {
        return mVideoPaths;
    }

    public List<String> getVideoNames() {
        return mVideoNames;
    }

}
