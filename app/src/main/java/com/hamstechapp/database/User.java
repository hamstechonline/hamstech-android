package com.hamstechapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @ColumnInfo(name = "name")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ColumnInfo(name = "phone")
    public String phone;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "uid")
    public int uid;

    public User(int uid, String name, String phone){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
    }

    public int getId(){
        return uid;
    }
}
