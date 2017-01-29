package com.example.artiik92.bitbucket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by artiik92 on 28.01.2017.
 */

public class RssChannels extends Activity {

    private TextView selectedChannelTitle;
    private ListView articlesList;
    private int channelId;
    private String channelTitle;
    private String channelUrl;
    private DbAdapter dbAdapter;
    private Cursor cursor;
    private SimpleCursorAdapter cursorAdapter;
    private boolean refreshed = false;
    private UpdateBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles);
        selectedChannelTitle = (TextView) findViewById(R.id.selectedchannel);
        articlesList = (ListView) findViewById(R.id.articleslist);
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        Intent intent = getIntent();
        channelId = intent.getIntExtra("id", 0);
        channelTitle = intent.getStringExtra("title");
        channelUrl = intent.getStringExtra("url");
        selectedChannelTitle.setText(channelTitle);

        broadcastReceiver = new UpdateBroadcastReceiver();
        intentFilter = new IntentFilter(UpdateService.key);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(broadcastReceiver, intentFilter);

        refreshArticles();
        showArticles();
    }

    @SuppressWarnings("deprecation")
    private void showArticles() {
        cursor = dbAdapter.getAllArticles(channelId);
        startManagingCursor(cursor);
        String[] from = new String[] {
                dbAdapter.KEY_A_TITLE,
                dbAdapter.KEY_A_URL,
                dbAdapter.KEY_A_DATE
        };
        int[] to = new int[] {
                R.id.articletitle,
                R.id.articleurl,
                R.id.articledate
        };
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.articles_adapter, cursor, from, to);
        articlesList.setAdapter(cursorAdapter);
        articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), Web.class);
                cursor.moveToPosition(position);
                String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                intent.putExtra("url", url);
                intent.putExtra("description", description);
                startActivity(intent);
            }
        });
        if (refreshed) {
            Toast toast = Toast.makeText(getApplicationContext(), "Channel has been successfully refreshed", Toast.LENGTH_SHORT);
            toast.show();
            refreshed = false;
        }
    }

    public void refreshChannel(View view) {
        refreshArticles();
    }

    public void refreshArticles() {
        Toast toast = Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT);
        toast.show();
        Intent newIntent = new Intent(this, UpdateService.class);
        newIntent.putExtra("channelTitle", channelTitle);
        newIntent.putExtra("channelUrl", channelUrl);
        newIntent.putExtra("channelId", channelId);
        startService(newIntent);
    }

    public class UpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            String title = intent.getStringExtra("channelTitle");
            if (result.equals("ok")) {
                refreshed = true;
                showArticles();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Ooops, something went wrong", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver, intentFilter);
        super.onResume();
    }
}
