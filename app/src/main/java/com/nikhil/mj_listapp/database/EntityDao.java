package com.nikhil.mj_listapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EntityDao
{
	@Query("SELECT * FROM EntityTable")
	LiveData<List<EntityTable>> getAll();

	@Insert
	void insert(EntityTable entities);

	@Query("SELECT count(uid) FROM EntityTable")
	int getDataCount();
}
