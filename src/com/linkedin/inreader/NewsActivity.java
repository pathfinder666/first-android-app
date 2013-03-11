package com.linkedin.inreader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.support.v4.app.NavUtils;

public class NewsActivity extends Activity {

	private String _skillName;
	private boolean _found = false;
	private Handler _handler;
	private JSONArray _result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_skillName = getIntent().getStringExtra(MainActivity.SKILL_NAME);
		_handler = new Handler();
		
		
		setContentView(R.layout.activity_news);
		
		TextView textView = (TextView) findViewById(R.id.newsTopicName);
		textView.setText("News about " + _skillName);
		textView.setGravity(Gravity.CENTER);
		
		checkUpdate.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_news, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Thread checkUpdate = new Thread() {
        public void run() {
        	HttpClient httpclient = new DefaultHttpClient();

        	String url = "http://172.16.20.203:8080/febHackDay/getNewsTopics?newsTopic="+ _skillName.replace(" ", "%20");
        	
            // Prepare a request object
            HttpGet httpget = new HttpGet(url); 

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpget);

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();
                // If the response does not enclose an entity, there is no need
                // to worry about connection release

                if (entity != null) {

                    // A Simple JSON Response Read
                    InputStream instream = entity.getContent();
                    _result = (JSONArray) JSONValue.parse(convertStreamToString(instream));
                    // now you have the string representation of the HTML request
                    
                    instream.close();
                }
                _handler.post(showUpdate);

            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
    };
    
    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	
	private Runnable showUpdate = new Runnable(){
        public void run(){
        	
        	for(int i = 0; i < _result.size(); i++)
        	{	
        		try {
        			JSONObject object = (JSONObject) _result.get(i);
            		addRow(object);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        }
    };
    
    public void addRow(final JSONObject object) {

		LinearLayout linear = (LinearLayout) findViewById(R.id.NewsLayout);
		
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = (View) inflater.inflate(R.layout.news, null, false);
		
		TextView newsItem = (TextView) rowView.findViewById(R.id.news);
		newsItem.setText(object.get("title").toString());	
		
		
		rowView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = object.get("url").toString();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
		linear.addView(rowView);
		linear.addView(getHorSep());
    }
    
    public View getHorSep() {
		LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, 1);
		View view1 = new View(this);
		view1.setLayoutParams(lparams);
		view1.setBackgroundColor(Color.DKGRAY);

		return view1;
	}
}
