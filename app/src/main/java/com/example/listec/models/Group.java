package com.example.listec.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {

    private String status;
    private String title;
    private String group_id;


    public Group(String title, String group_id, String status) {
        this.status = status;
        this.title = title;
        this.group_id = group_id;
    }

    public Group() {

    }

    protected Group(Parcel in) {
        status = in.readString();
        title = in.readString();
        group_id = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Group{" +
                "title='" + title + '\'' +
                ", group_id='" + group_id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(title);
        dest.writeString(group_id);
    }
}
