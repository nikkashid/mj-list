package com.nikhil.mj_listapp.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.nikhil.mj_listapp.database.AppDataBase;
import com.nikhil.mj_listapp.database.EntityDao;
import com.nikhil.mj_listapp.database.EntityTable;

import java.util.List;

public class EntityRepository
{
	private EntityDao entityDao;

	private LiveData<List<EntityTable>> allEntities;

	public EntityRepository(Application application)
	{
		AppDataBase appDataBase = AppDataBase.getInstance(application);
		entityDao = appDataBase.entityDao();
		allEntities = entityDao.getAll();
	}

	public LiveData<List<EntityTable>> getAll()
	{
		return entityDao.getAll();
	}

	public void insert(EntityTable entityTable)
	{
		new InsertDataAsyncTask(entityDao).execute(entityTable);
	}

	public int getDataCount()
	{
		return entityDao.getDataCount();
	}

	private static class InsertDataAsyncTask extends AsyncTask<EntityTable, Void, Void>
	{
		private EntityDao entityDao;

		private InsertDataAsyncTask(EntityDao entityDao)
		{
			this.entityDao = entityDao;
		}

		@Override
		protected Void doInBackground(EntityTable... entityTable)
		{
			entityDao.insert(entityTable[0]);
			return null;
		}
	}
}
