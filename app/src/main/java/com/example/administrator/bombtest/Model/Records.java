package com.example.administrator.bombtest.Model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class Records extends BmobObject {
    private String user_id;
    private String group_id;
    private BmobGeoPoint location;
    private int time_slot;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public int getTime_slot() {
        return time_slot;
    }

    public void setTime_slot(int time_slot) {
        this.time_slot = time_slot;
    }
}
