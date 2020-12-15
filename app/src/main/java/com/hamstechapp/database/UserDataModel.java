package com.hamstechapp.database;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class UserDataModel {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;

}
