package com.lucasdev.stereodroid;

import org.scribe.model.Token;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class Stereodroid extends Activity {

	public static final String TAG = "Stereodroid";
	public static final String CALLBACK_URL = "stereodroid://oauth";
	
	private static SharedPreferences settings;
	private static StereodroidOAuth oauth = StereodroidOAuth.getInstance();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		settings = getSharedPreferences(TAG, MODE_PRIVATE);
		
		String userToken = settings.getString("userToken", null);
		String tokenSecret = settings.getString("tokenSecret", null);
		Log.i(TAG, "Token: "+userToken);
		if (userToken == null) {
			Log.i(TAG, "Token not found");
			doOauth();
		} else {
			Log.i(TAG, "Token found");
			oauth.setAccessToken(userToken, tokenSecret);
			startActivity(new Intent(Stereodroid.this,Search.class));
		}
	}
	
	public void doOauth() {
		try {
			String authUrl = oauth.getAuthorizeUrl();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(authUrl)));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	public static void saveToken(Token accessToken) {
		Log.i(TAG, "Saving token");
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userToken", accessToken.getToken());
		editor.putString("tokenSecret", accessToken.getSecret());
		editor.commit();
		Log.i(TAG, "Token saved "+accessToken);
	}
	
	public static void removeToken() {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userToken", null);
		editor.putString("tokenSecret", null);
		editor.commit();
	}

	public static Token getAccessToken(String verifier) {
		return oauth.doValidate(verifier);
	}
}