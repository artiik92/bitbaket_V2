package com.example.artiik92.bitbucket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainRssChannels extends AppCompatActivity {

    SharedPreferences prefs = null;

    private ListView channelsList;
    private DbAdapter dbAdapter;
    private Cursor cursor;
    private SimpleCursorAdapter cursorAdapter;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_channels);
        channelsList = (ListView) findViewById(R.id.channelslist);
        dbAdapter = new DbAdapter(this);
        prefs = getSharedPreferences("com.example.artiik92.bitbucket", MODE_PRIVATE);
        dbAdapter.open();
        if (prefs.getBoolean("firstrun", true)) {
            dbAdapter.addChannel("top", "https://habrahabr.ru/rss/best/");
            dbAdapter.addChannel("Weekly", "https://habrahabr.ru/rss/best/weekly/");
            dbAdapter.addChannel("Monthly", "https://habrahabr.ru/rss/best/monthly/");
            dbAdapter.addChannel("All Time", "https://habrahabr.ru/rss/best/alltime/");
            prefs.edit().putBoolean("firstrun", false).apply();
        }
        cursor = dbAdapter.getAllChannels();

        startManagingCursor(cursor);
        String[] from = new String[] {DbAdapter.KEY_TITLE, DbAdapter.KEY_URL};
        int[] to = new int[] {R.id.channelTitle, R.id.channelUrl};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.channel_adapter, cursor, from, to);
        channelsList.setAdapter(cursorAdapter);
        registerForContextMenu(channelsList);

        channelsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), RssChannels.class);
                cursor.moveToPosition(position);
                int cId = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String url = cursor.getString(2);
                intent.putExtra("title", title);
                intent.putExtra("url", url);
                intent.putExtra("id", cId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if ("Edit".equals(item.getTitle())) {
            cursor.moveToPosition((int) adapterContextMenuInfo.position);
            int id = (int) adapterContextMenuInfo.id;
            String title = cursor.getString(1);
            String url = cursor.getString(2);
            editChannel(false, title, url, id);
        } else if ("Delete".equals(item.getTitle())){
            dbAdapter.deleteChannel((int) adapterContextMenuInfo.id);
            cursor.requery();
        } else {
            return false;
        }
        return true;
    }

    public void addNewChannel(View view) {
        editChannel(true, "", "https://", 5);
    }


    public void editChannel(boolean isNew, String title, String url, int id) {
        Intent intent = new Intent(this, EditRssChannel.class);
        if (isNew) {
            intent.putExtra("action", "add");
        } else {
            intent.putExtra("action", "edit");
        }
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
