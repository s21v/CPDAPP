package com.cpd.yuqing.db.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by s21v on 2017/11/13.
 */

public class Channel implements Parcelable{
    private String id;
    private String name;
    private int sortNum;

    public Channel(String id, String name) {
        this.id = id;
        this.name = name;
        this.sortNum = 0;   //0：显示在栏目条上，默认
    }

    public Channel(String id, String name, int sort) {
        this.id = id;
        this.name = name;
        this.sortNum = sort;
    }

    private Channel(Parcel in) {
        id = in.readString();
        name = in.readString();
        sortNum = in.readInt();
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        if (!id.equals(channel.id)) return false;
        return name.equals(channel.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sortNum=" + sortNum +
                '}';
    }

    public static Creator<Channel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(sortNum);
    }
}
