package com.lucasdev.stereodroid;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Player extends Activity{
	
	public static String TAG = "StereodroidPlayer";
	private static MediaPlayer mp = new MediaPlayer();
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		tv = (TextView)findViewById(R.id.TextView01);
		tv.setText("Player here");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "On New intent");
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG, "On resume: "+String.valueOf(getInstanceCount()));
		String songUrl = getIntent().getExtras().getString("song_url");
		play(songUrl);
		super.onResume();
	}
	
	private void play(final String songUrl){
		Runnable rPlay = new Runnable() {
			
			@Override
			public void run() {
				try {
					mp.reset();
					mp.setDataSource(songUrl);
					mp.prepare();
					mp.start();
				} catch (Exception e) {
					Log.e(TAG, "Error "+e.getMessage());
					mp.reset();
				}
			}
		};
		new Thread(rPlay).start();
	}
}
