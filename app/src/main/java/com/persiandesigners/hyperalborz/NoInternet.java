package com.persiandesigners.hyperalborz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NoInternet extends AppCompatActivity {
	Typeface typeface2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nointernet);
		typeface2= Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");
		callmain();

	}

	private void callmain() {
		if (!NetworkAvailable.isNetworkAvailable(NoInternet.this)) {
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setTypeface(typeface2);
			tv.setText(DariGlyphUtils.reshapeText(getString(R.string.nointernet)));

			Button bt = (Button) findViewById(R.id.button1);
			bt.setTypeface(typeface2);
			bt.setText(DariGlyphUtils.reshapeText(getString(R.string.checkagain)));
			bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					callmain();
				}
			});
		}else{
			Bundle bl=getIntent().getExtras();
			if(bl!=null){
				Intent in = null;
				if( bl.getString("from").equals("fistActivity")){
					in=new Intent (this,FistActiivty.class);
					in.putExtra("idish",bl.getString("idish"));
				}
				startActivity(in);
			}else
				startActivity(new Intent(this,FistActiivty.class));
			finish();
		}
	}
}
