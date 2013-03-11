package com.linkedin.inreader;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.TextSize;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String SKILL_NAME = "skillName";
	private PopupWindow _popPopupWindow;
	private List<String> _newsTopics = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		_newsTopics.clear();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void displayPopup(View view) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View popView = (View) inflater.inflate(R.layout.add_popup, null, false);
		Button okButton = (Button) popView.findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addRow(v);
			}
		});
		
		Button cancelButton = (Button) popView.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_popPopupWindow.dismiss();
				_popPopupWindow = null;
			}
		});

		_popPopupWindow = new PopupWindow(popView, 400, 300, true);
		_popPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		_popPopupWindow.setOutsideTouchable(false);
		_popPopupWindow.setFocusable(true);

		_popPopupWindow.showAtLocation(this.findViewById(R.id.LinearLayout1),
				Gravity.CENTER, 0, 0);

	}

	public void addRow(View view) {

		EditText editText = (EditText) _popPopupWindow.getContentView().findViewById(R.id.skillInput);
		
		if(!_newsTopics.contains(editText.getText().toString()))
		{
			LinearLayout linear = (LinearLayout) findViewById(R.id.LinearLayout1);

			
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = (View) inflater.inflate(R.layout.row, null, false);
			
			TextView skillName = (TextView) rowView.findViewById(R.id.skillName);
			skillName.setText(editText.getText().toString());
			final Context mainActivity = this;
			
			rowView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mainActivity, NewsActivity.class);
					
					TextView skillName = (TextView) v.findViewById(R.id.skillName);
					intent.putExtra(SKILL_NAME, skillName.getText().toString());
					startActivity(intent);
				}
			});
			
			linear.addView(rowView);
			linear.addView(getHorSep());
		}

		_popPopupWindow.dismiss();
		_popPopupWindow = null;
	}

	public void cancelPoup() {
		if (_popPopupWindow != null) {
			_popPopupWindow.dismiss();
		}

		_popPopupWindow = null;
	}

	public View getHorSep() {
		LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, 1);
		View view1 = new View(this);
		view1.setLayoutParams(lparams);
		view1.setBackgroundColor(Color.DKGRAY);

		return view1;
	}
}
