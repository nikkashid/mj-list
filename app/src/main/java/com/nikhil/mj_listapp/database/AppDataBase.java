package com.nikhil.mj_listapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { EntityTable.class }, version = 2)
public abstract class AppDataBase extends RoomDatabase
{
	private static final String TAG = "AppDataBase";

	private static AppDataBase instance;

	public abstract EntityDao entityDao();

	public static synchronized AppDataBase getInstance(Context context)
	{
		if (instance == null)
		{
			instance = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, TAG)
					.fallbackToDestructiveMigration().build();
		}

		return instance;
	}
}
