package com.example.videoplay;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.videoplay.bean.VideoInfo;
import com.example.videoplay.util.VideoSearch;
import com.example.videoplay.util.VideoSearchUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面，用ListActivity显示path下所有视频文件
 *
 * @author ly
 */
public class MainActivity extends ListActivity {
    private Context mContext = this;
    private List<String> mListPath;
    private int mClickPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        registerForContextMenu(getListView());
        showFromSQL();
    }

    /**
     * 查找系统数据库，查找视频
     */
    private void showFromSQL() {
        mListPath = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        List<VideoInfo> videoInfos = new VideoSearch(mContext).getmVideoLists();

        for (int i = 0; i < videoInfos.size(); i++) {
            mListPath.add(videoInfos.get(i).path);
            listName.add(videoInfos.get(i).name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listName);
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(itemLongClick);
    }

    /**
     * 通过查找文件寻找视频，文件过多时很耗时间
     */
    private void showFromSearchFile() {
        String path = Environment.getExternalStorageDirectory().getPath();
        mListPath = new VideoSearchUtil(path).getVideoPaths();
        List<String> listName = new VideoSearchUtil(path).getVideoNames();//列表显示视频名称

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listName);
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(itemLongClick);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mClickPos = position;
        openContextMenu(getListView());
    }


    private AdapterView.OnItemLongClickListener itemLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(mContext, "路径:\n" + mListPath.get(position), Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_main_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_context1:
                // 1.调用系统自带的播放器
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setDataAndType(Uri.parse(mListPath.get(mClickPos)), "video/*");
                startActivity(i1);
                break;
            case R.id.menu_contex2:
                //2.使用VideoView播放
                Intent i2 = new Intent(mContext, VideoViewActivity.class);
                i2.putExtra("path", mListPath.get(mClickPos));
                startActivity(i2);
                break;
            case R.id.menu_context3:
                Intent i3 = new Intent(mContext, SurfaceMediaActivity.class);
                i3.putExtra("path", mListPath.get(mClickPos));
                startActivity(i3);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sql:
                showFromSQL();
                break;
            case R.id.action_file:
                showFromSearchFile();
                break;
        }
        return true;
    }
}
