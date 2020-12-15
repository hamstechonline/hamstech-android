package com.hamstechapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao userDao();
}
