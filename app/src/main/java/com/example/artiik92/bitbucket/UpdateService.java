package com.example.artiik92.bitbucket;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Created by artiik92 on 28.01.2017.
 */

public class UpdateService  extends IntentService {

    public static final String key = "UpdateService";
    private static final int period = 15 * 60 * 1000;

    private String channelTitle;
    private String channelUrl;
    private int channelId;

    private ArrayList<String> titles;
    private ArrayList<String> urls;
    private ArrayList<String> descriptions;
    private ArrayList<String> dates;

    public UpdateService() {
        super("Update Service");
    }

    @Override
    public void onHandleIntent(Intent intent) {

        channelTitle = intent.getStringExtra("channelTitle");
        channelUrl = intent.getStringExtra("channelUrl");
        channelId = intent.getIntExtra("channelId", 0);
        titles = new ArrayList<String>();
        urls = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        dates = new ArrayList<String>();
        boolean success = false;
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(channelUrl));
            HttpEntity httpEntity = httpResponse.getEntity();
            String xml = EntityUtils.toString(httpEntity, "UTF-8");
            InputSource is = new InputSource(new StringReader(xml));
            saxParser.parse(is, new RssHendler(titles, urls, descriptions, dates));
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent broadcast = new Intent();
        broadcast.setAction(key);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        if (success) {
            DbAdapter dbAdapter = new DbAdapter(this);
            dbAdapter.open();
            dbAdapter.deleteArticles(channelId);
            for (int i = 0; i < titles.size(); ++i) {
                dbAdapter.addArticle(channelId, titles.get(i), urls.get(i), descriptions.get(i), dates.get(i));
            }
            dbAdapter.close();
            broadcast.putExtra("result", "ok");
        } else {
            broadcast.putExtra("result", "error");
        }
        broadcast.putExtra("channelTitle", channelTitle);
        sendBroadcast(broadcast);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + period, period, pi);
    }
}
