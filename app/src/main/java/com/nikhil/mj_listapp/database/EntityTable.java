package com.nikhil.mj_listapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityTable
{
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "data")
    public String data;
}
