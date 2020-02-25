package com.nikhil.mj_listapp.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nikhil.mj_listapp.R;
import com.nikhil.mj_listapp.database.EntityTable;
import com.nikhil.mj_listapp.network.RetrofitInterface;
import com.nikhil.mj_listapp.utilities.UtilityClass;
import com.nikhil.mj_listapp.viewmodel.EntityViewModel;
import com.nikhil.mj_listapp.views.adapters.RecyclerAdapter;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.IAdapterCallBack
{
	private static final String TAG = "MainActivity";

	DownloadFileTask downloadFileTask;

	TextView txtProgressPercent;

	ProgressBar progressBar;

	EntityViewModel entityViewModel;

	RecyclerView recyclerView;

	RecyclerAdapter recyclerAdapter;

	LinearLayout ll_downloadPart;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();

		entityViewModel = ViewModelProviders.of(this).get(EntityViewModel.class);
		entityViewModel.getAll().observe(this, new Observer<List<EntityTable>>()
		{
			@Override
			public void onChanged(List<EntityTable> entityTables)
			{
				Log.i(TAG, "askForPermission: listSize " + entityTables.size());
				/*for (int i = 0; i < entityTables.size(); i++)
				{
					Log.d(TAG, "onChanged: " + entityTables.get(i).getData());
				}*/

				recyclerView.setVisibility(View.VISIBLE);
				ll_downloadPart.setVisibility(View.GONE);
				recyclerAdapter.setList(entityTables);
			}
		});

		askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
	}

	void initViews()
	{
		txtProgressPercent = findViewById(R.id.txtProgressPercent);
		progressBar = findViewById(R.id.progressBar);
		ll_downloadPart = findViewById(R.id.ll_downloadPart);
		recyclerView = findViewById(R.id.rv_albums);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);

		recyclerAdapter = new RecyclerAdapter(this, MainActivity.this);
		recyclerView.setAdapter(recyclerAdapter);
	}

	private void checkDBData()
	{
		final AtomicInteger fcount = new AtomicInteger();
		try
		{
			Thread t = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					int num = entityViewModel.getDataCount();
					fcount.set(num);
				}
			});
			t.setPriority(10);
			t.start();
			t.join();
		}
		catch (Exception e)
		{
			Log.e(TAG, "onCreate: ", e);
		}

		int dbCount = fcount.get();
		if (dbCount > 0)
		{
			Log.d(TAG, "onCreate: dbCount = " + dbCount);
			recyclerView.setVisibility(View.VISIBLE);
			ll_downloadPart.setVisibility(View.GONE);
		}
		else
		{
			recyclerView.setVisibility(View.GONE);
			ll_downloadPart.setVisibility(View.VISIBLE);
			downloadFile();
		}
	}

	private void downloadFile()
	{
		RetrofitInterface downloadService = createService(RetrofitInterface.class, "https://itunes.apple.com/");
		Call<ResponseBody> call = downloadService.downloadFileByUrl("search?term=Michael+jackson");

		call.enqueue(new Callback<ResponseBody>()
		{
			@Override
			public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response)
			{
				if (response.isSuccessful())
				{
					Log.d(TAG, "Got the body for the file");

					Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

					downloadFileTask = new DownloadFileTask();
					downloadFileTask.execute(response.body());
				}
				else
				{
					Log.d(TAG, "Connection failed " + response.errorBody());
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t)
			{
				t.printStackTrace();
				Log.e(TAG, t.getMessage());
			}
		});
	}

	public <T> T createService(Class<T> serviceClass, String baseUrl)
	{
		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(new OkHttpClient.Builder().build()).build();
		return retrofit.create(serviceClass);
	}

	@Override
	public void itemClicked(String artistData, ImageView iv_albumImage, TextView artistName)
	{
		Intent intent = new Intent(this, DetailsActivity.class);
		intent.putExtra("artistObject", artistData);
		intent.putExtra("imageTransition", "imageTransition");
		intent.putExtra("textTransition", "textTransition");

		Pair<View, String> p1 = Pair.create((View) iv_albumImage, "imageTransition");
		Pair<View, String> p2 = Pair.create((View) artistName, "textTransition");

		ActivityOptions options = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
		{
			options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, p1, p2);
		}

		if (options != null)
		{
			startActivity(intent, options.toBundle());
		}
		else
		{
			startActivity(intent);
		}
	}

	private class DownloadFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(ResponseBody... urls)
		{
			//Copy you logic to calculate progress and call
			saveToDisk(urls[0], "MJList.txt");
			return null;
		}

		protected void onProgressUpdate(Pair<Integer, Long>... progress)
		{
			Log.d(TAG, progress[0].second + " ");

			if (progress[0].first == 100)
				Toast.makeText(getApplicationContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();

			if (progress[0].second > 0)
			{
				int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
				progressBar.setProgress(currentProgress);
				txtProgressPercent.setText("Progress " + currentProgress + "%");
			}

			if (progress[0].first == -1)
			{
				Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
			}
		}

		public void doProgress(Pair<Integer, Long> progressDetails)
		{
			publishProgress(progressDetails);
		}

		@Override
		protected void onPostExecute(String result)
		{

		}
	}

	private void saveToDisk(ResponseBody body, String filename)
	{
		try
		{
			File destinationFile = new File(this.getFilesDir(), filename);

			//File file = new File(this.getFilesDir(),"mydir");

			InputStream inputStream = null;
			OutputStream outputStream = null;

			try
			{
				inputStream = body.byteStream();
				outputStream = new FileOutputStream(destinationFile);
				byte data[] = new byte[4096];
				int count;
				int progress = 0;
				long fileSize = body.contentLength();
				Log.d(TAG, "File Size=" + fileSize);
				while ((count = inputStream.read(data)) != -1)
				{
					outputStream.write(data, 0, count);
					progress += count;
					Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
					downloadFileTask.doProgress(pairs);
					Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
				}

				outputStream.flush();

				Log.d(TAG, destinationFile.getParent());
				Pair<Integer, Long> pairs = new Pair<>(100, 100L);
				downloadFileTask.doProgress(pairs);

				sendDataFromFileToRoom(filename);

				return;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
				downloadFileTask.doProgress(pairs);
				Log.d(TAG, "Failed to save the file!");
				return;
			}
			finally
			{
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.d(TAG, "Failed to save the file!");
			return;
		}
	}

	private void sendDataFromFileToRoom(String filename)
	{
		JSONArray resultArray = UtilityClass.readDataFromFile(this, filename);

		if (resultArray.length() > 0)
		{
			for (int i = 0; i < resultArray.length(); i++)
			{
				try
				{
					String element = resultArray.getString(i);
					EntityTable entityTable = new EntityTable(element);
					entityViewModel.insertEntity(entityTable);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void askForPermission(String permission, Integer requestCode)
	{
		if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
		{
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission))
			{
				ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
			}
			else
			{
				ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
			}
		}
		else if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED)
		{
			Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
		}
		else
		{
			checkDBData();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
		{
			if (requestCode == 101)
				Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
			checkDBData();
		}
		else
		{
			Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
		}
	}

}
