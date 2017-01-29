package com.example.artiik92.bitbaket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by artiik92 on 28.01.2017.
 */

public class EditRssChannel extends Activity {

    private String title;
    private String url;
    private String action;
    private int id;
    private EditText titleField;
    private EditText urlField;
    private DbAdapter dbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_channel);
        Intent intent = getIntent();
        titleField = (EditText) findViewById(R.id.newchanneltitle);
        urlField = (EditText) findViewById(R.id.newchannelurl);
        url = intent.getStringExtra("url");
        action = intent.getStringExtra("action");
        title = intent.getStringExtra("title");
        id = intent.getIntExtra("id", 0);
        dbAdapter = new DbAdapter(this);
        titleField.setText(title);
        urlField.setText(url);
    }

    public void backToChannels(View view) {
        dbAdapter.open();
        String newTitle = titleField.getText().toString();
        String newUrl = urlField.getText().toString();
        if ("edit".equals(action)) {
            dbAdapter.deleteChannel(id);
        }
        if (newTitle.length() == 0 || newUrl.length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please, fill all the fields", Toast.LENGTH_LONG);
            toast.show();
        } else {
            dbAdapter.addChannel(newTitle, newUrl);
        }
        dbAdapter.close();
        finish();
    }

}
