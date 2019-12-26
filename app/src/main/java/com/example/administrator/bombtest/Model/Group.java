package com.example.administrator.bombtest.Model;

import java.io.Serializable;
import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class Group extends BmobObject implements Serializable{
    private String name;
    private BmobRelation members;
    private Student owner;
    private BmobGeoPoint location;
    private String start_date;
    private String end_date;
    private ArrayList<String> starts_time;
    private ArrayList<String> ends_time;
    private int group_number;
    private String notice;
    private String location_describe;

    public void setLocation_describe(String location_describe) {
        this.location_describe = location_describe;
    }

    public void setGroup_number(int group_number) {
        this.group_number = group_number;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getLocation_describe() {
        return location_describe;
    }

    public String getNotice() {
        return notice;
    }

    public int getGroup_number() {
        return group_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BmobRelation getMembers() {
        return members;
    }

    public void setMembers(BmobRelation members) {
        this.members = members;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public Student getOwner() {
        return owner;
    }

    public void setOwner(Student owner) {
        this.owner = owner;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public ArrayList<String> getStarts_time() {
        return starts_time;
    }

    public void setStarts_time(ArrayList<String> starts_time) {
        this.starts_time = starts_time;
    }

    public ArrayList<String> getEnds_time() {
        return ends_time;
    }

    public void setEnds_time(ArrayList<String> ends_time) {
        this.ends_time = ends_time;
    }
}
