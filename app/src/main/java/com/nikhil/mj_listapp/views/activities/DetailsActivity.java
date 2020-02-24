package com.nikhil.mj_listapp.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nikhil.mj_listapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity
{
	private static final String TAG = "DetailsActivity";

	private ImageView artworkUrl100;

	private TextView artistName;

	private TextView collectionPrice;

	private TextView trackPrice;

	private TextView trackCount;

	private TextView collectionName;

	private TextView trackName;

	private TextView collectionCensoredName;

	private TextView trackCensoredName;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		if (getIntent() != null)
		{
			try
			{
				String data = getIntent().getStringExtra("artistObject");
				String imageTransition = getIntent().getStringExtra("imageTransition");
				String textTransition = getIntent().getStringExtra("textTransition");
				Log.d(TAG, "onCreate: intent data \n" + data);
				initViews();
				setData(data, imageTransition, textTransition);
			}
			catch (Exception e)
			{
				Log.e(TAG, "onCreate: ", e);
			}
		}
	}

	private void setData(String data, String transitionName, String textTransition)
	{
		try
		{
			JSONObject dataJson = new JSONObject(data);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				artistName.setTransitionName(textTransition);
			}
			artistName.setText(dataJson.getString("artistName"));

			collectionPrice.setText(dataJson.getString("collectionPrice"));
			trackPrice.setText(dataJson.getString("trackPrice"));
			trackCount.setText(dataJson.getString("trackCount"));
			collectionName.setText(dataJson.getString("collectionName"));
			trackName.setText(dataJson.getString("trackName"));
			collectionCensoredName.setText(dataJson.getString("collectionCensoredName"));
			trackCensoredName.setText(dataJson.getString("trackCensoredName"));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				artworkUrl100.setTransitionName(transitionName);
			}

			Picasso.with(this).load(dataJson.getString("artworkUrl100")).noFade().into(artworkUrl100, new Callback()
			{
				@Override
				public void onSuccess()
				{
					supportStartPostponedEnterTransition();
				}

				@Override
				public void onError()
				{
					supportStartPostponedEnterTransition();
				}
			});
		}
		catch (Exception e)
		{
			Log.e(TAG, "setData: ", e);
		}

	}

	private void initViews()
	{
		artworkUrl100 = (ImageView) findViewById(R.id.artworkUrl100);
		artistName = (TextView) findViewById(R.id.artistName);
		collectionPrice = (TextView) findViewById(R.id.collectionPrice);
		trackPrice = (TextView) findViewById(R.id.trackPrice);
		trackCount = (TextView) findViewById(R.id.trackCount);
		collectionName = (TextView) findViewById(R.id.collectionName);
		trackName = (TextView) findViewById(R.id.trackName);
		collectionCensoredName = (TextView) findViewById(R.id.collectionCensoredName);
		trackCensoredName = (TextView) findViewById(R.id.trackCensoredName);
	}
}
