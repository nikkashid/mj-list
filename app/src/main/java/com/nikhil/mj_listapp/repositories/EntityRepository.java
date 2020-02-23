package com.nikhil.mj_listapp.repositories;

import android.app.Application;

import com.nikhil.mj_listapp.database.AppDataBase;
import com.nikhil.mj_listapp.database.EntityDao;

public class EntityRepository
{
    private EntityDao entityDao;

    public EntityRepository(Application application)
    {
        AppDataBase appDataBase = AppDataBase.getInstance(application);

    }
}
