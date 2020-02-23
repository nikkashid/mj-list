package com.nikhil.mj_listapp.utilities;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

public class UtilityClass
{
	private static final String TAG = "UtilityClass";

	public static JSONArray readDataFromFile(String filename)
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
			//Log.d(TAG, "content : " + content.trim());

			return parseData(content.trim());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

        return null;
    }

	private static JSONArray parseData(String trim)
	{
		try
		{
			JSONObject responseJson = new JSONObject(trim);
			if (responseJson.has("results"))
			{
				JSONArray results = responseJson.getJSONArray("results");
				return results;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
