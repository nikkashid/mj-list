package com.nikhil.mj_listapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EntityDao
{
	//Push check
	@Query("SELECT * FROM EntityTable")
	List<EntityTable> getAll();

	@Insert
    void insert(EntityTable entities);
}
