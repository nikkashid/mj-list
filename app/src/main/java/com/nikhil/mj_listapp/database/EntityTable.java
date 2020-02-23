package com.nikhil.mj_listapp.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityTable
{
	@PrimaryKey(autoGenerate = true)
	public int uid;

	@ColumnInfo(name = "data")
	public String data;

	public EntityTable(String data)
	{
		this.data = data;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public String getData()
	{
		return data;
	}

	@NonNull
	@Override
	public String toString()
	{
		return super.toString();
	}
}
