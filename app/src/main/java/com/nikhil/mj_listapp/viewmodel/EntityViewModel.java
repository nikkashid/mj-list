package com.nikhil.mj_listapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nikhil.mj_listapp.database.EntityTable;
import com.nikhil.mj_listapp.repositories.EntityRepository;

import java.util.List;

public class EntityViewModel extends AndroidViewModel
{
    EntityRepository entityRepository;
    LiveData<List<EntityTable>> entityTables;

	public EntityViewModel(@NonNull Application application)
	{
		super(application);
        entityRepository = new EntityRepository(getApplication());
        entityRepository.getAll();
	}

	public LiveData<List<EntityTable>> getAll()
    {
        return entityRepository.getAll();
    }

    public void insertEntity(EntityTable entityTable)
    {
        entityRepository.insert(entityTable);
    }

    public int getDataCount()
    {
        return entityRepository.getDataCount();
    }
}
