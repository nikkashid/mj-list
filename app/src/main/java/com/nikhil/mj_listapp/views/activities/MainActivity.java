package com.nikhil.mj_listapp.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nikhil.mj_listapp.R;
import com.nikhil.mj_listapp.database.EntityTable;
import com.nikhil.mj_listapp.network.RetrofitInterface;
import com.nikhil.mj_listapp.repositories.EntityRepository;
import com.nikhil.mj_listapp.utilities.UtilityClass;
import com.nikhil.mj_listapp.viewmodel.EntityViewModel;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "MainActivity";

	DownloadFileTask downloadFileTask;

	TextView txtProgressPercent;

	ProgressBar progressBar;

	EntityRepository entityRepository;

	EntityViewModel entityViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);

		txtProgressPercent = findViewById(R.id.txtProgressPercent);
		progressBar = findViewById(R.id.progressBar);
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
			File destinationFile = new File(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

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
				readDataFromFile(filename);
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

	private void readDataFromFile(String filename)
	{
		try
		{
			File destinationFile = new File(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

			FileInputStream fis = new FileInputStream(destinationFile);
			byte[] buffer = new byte[10];
			StringBuilder sb = new StringBuilder();
			while (fis.read(buffer) != -1)
			{
				sb.append(new String(buffer));
				buffer = new byte[10];
			}

			fis.close();

			String content = sb.toString();
			Log.d(TAG, "content : " + content.trim());
		}
		catch (Exception e)
		{
			e.printStackTrace();
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

		JSONArray resultArray = UtilityClass.readDataFromFile("MJList.txt");

		entityViewModel = ViewModelProviders.of(this).get(EntityViewModel.class);
		entityViewModel.getAll().observe(this, new Observer<List<EntityTable>>()
		{
			@Override
			public void onChanged(List<EntityTable> entityTables)
			{
				//Log.i(TAG, "askForPermission: listSize " + entityTables.toString());
				for (int i = 0; i < entityTables.size(); i++)
				{
					Log.d(TAG, "onChanged: " + entityTables.get(i).getData());
				}
			}
		});

		for (int i = 0; i < resultArray.length(); i++)
		{
			try
			{
				String element = resultArray.getString(i);
				EntityTable entityTable = new EntityTable(element);
				//entityViewModel.insertEntity(entityTable);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
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
			downloadFile();
		}
		else
		{
			Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
		}
	}
}
