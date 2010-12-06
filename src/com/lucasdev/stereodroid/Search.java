package com.lucasdev.stereodroid;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Search extends ListActivity {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<JSONObject> m_orders = null;
	private OrderAdapter m_adapter;
	private Runnable viewOrders;
	private String query;
	public static String searchUrl = "http://www.stereomood.com/api/search.json?type=%s&q=%s";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
		m_orders = new ArrayList<JSONObject>();
		this.m_adapter = new OrderAdapter(this, R.layout.list_item, m_orders);
		setListAdapter(this.m_adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				JSONObject json = (JSONObject) parent.getItemAtPosition(position);
				try {
					String currentSongUrl = json.getString("audio_url");
					Intent i = new Intent(Search.this,Player.class);
					i.putExtra("song_url", currentSongUrl);
					startActivity(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	    Intent intent = getIntent();

	    if (Intent.ACTION_SEARCH.equals(intent.getAction()))
	      handleIntent(intent);
	    else
	    	onSearchRequested();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d("SEARCH", "on new intent");
	    setIntent(intent);
	    handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	query = intent.getStringExtra(SearchManager.QUERY);
	    	viewOrders = new Runnable() {
				@Override
				public void run() {
					getOrders();
				}
			};
			Thread thread = new Thread(null, viewOrders, "SearchBackground");
			thread.start();
			m_ProgressDialog = ProgressDialog.show(Search.this, "Please wait...",
					"Retrieving data ...", true);
	    }
	}

	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			if (m_orders != null && m_orders.size() > 0) {
				m_adapter.clear();
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < m_orders.size(); i++)
					m_adapter.add(m_orders.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private void getOrders() {
		try {
			if(query == null)
				query = "sad";
			Log.d("QUERY", query);
			JSONObject entries = getJson(buildSeachUrl(query));
			parseJsonForSearch(entries);
			Thread.sleep(5000);
			Log.i("ARRAY", "" + m_orders.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private class OrderAdapter extends ArrayAdapter<JSONObject> {

		private ArrayList<JSONObject> items;

		public OrderAdapter(Context context, int textViewResourceId,
				ArrayList<JSONObject> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_item, null);
			}
			JSONObject o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				try {
					if (tt != null) {
						tt.setText(o.getString("title"));
					}
					if (bt != null) {
						bt.setText(o.getString("artist"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return v;
		}
	}
	
	public JSONObject getJson(String url) {
		return Util.getJsonFromUrl(url);
	}
	
	public String buildSeachUrl(String q) {
		return String.format(searchUrl, "mood", q);
	}
	
	public void parseJsonForSearch(JSONObject entries) {
		JSONArray songs = null;
		m_orders = new ArrayList<JSONObject>();
		try {
			songs = entries.getJSONArray("songs");
			int i;
			for (i = 0; i < songs.length(); i++) {
				JSONObject song = songs.getJSONObject(i);
				Log.d("SONGS", song.getString("title"));
				m_orders.add(song);
			}
			runOnUiThread(returnRes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}