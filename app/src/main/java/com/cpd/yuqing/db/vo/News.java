package com.cpd.yuqing.db.vo;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by s21v on 2017/12/4.
 */

public class News implements Parcelable{
    private String news_id;		//新闻ID
    private int user_id; //用户ID
    private String channel_id;	//栏目ID
    private String homePageTitle;	//栏目页标题
    private String contentPageTitle;	//内容页标题
    private String pub_time;	//发布时间
    private String source;		//来源
    private String author;		//作者
    private String poster;		//责任编辑
    private String content;		//正文内容
    private String url;			//新闻的url链接
    private String picUrls;	//新闻中的图片链接
    private int favorite;   //是否收藏
    private int thumbUp;    //是否点赞

    public News(Cursor cursor) {
        this.news_id = cursor.getString(cursor.getColumnIndex("_id"));
        this.user_id = cursor.getInt(cursor.getColumnIndex("userId"));
        this.channel_id = cursor.getString(cursor.getColumnIndex("channelId"));
        this.homePageTitle = cursor.getString(cursor.getColumnIndex("homePageTitle"));
        this.contentPageTitle = cursor.getString(cursor.getColumnIndex("contentPageTitle"));
        this.pub_time = cursor.getString(cursor.getColumnIndex("pubTime"));
        this.source = cursor.getString(cursor.getColumnIndex("source"));
        this.author = cursor.getString(cursor.getColumnIndex("author"));
        this.poster = cursor.getString(cursor.getColumnIndex("poster"));
        this.content = cursor.getString(cursor.getColumnIndex("content"));
        this.url = cursor.getString(cursor.getColumnIndex("url"));
        this.picUrls = cursor.getString(cursor.getColumnIndex("picUrls"));
        this.favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
        this.thumbUp = cursor.getInt(cursor.getColumnIndex("thumbUp"));
    }

    protected News(Parcel in) {
        news_id = in.readString();
        channel_id = in.readString();
        homePageTitle = in.readString();
        contentPageTitle = in.readString();
        pub_time = in.readString();
        source = in.readString();
        author = in.readString();
        poster = in.readString();
        content = in.readString();
        url = in.readString();
        picUrls = in.readString();
        favorite = in.readInt();
        thumbUp = in.readInt();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getNews_id() {
        return news_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getHomePageTitle() {
        return homePageTitle;
    }

    public void setHomePageTitle(String homePageTitle) {
        this.homePageTitle = homePageTitle;
    }

    public String getContentPageTitle() {
        return contentPageTitle;
    }

    public String getPub_time() {
        return pub_time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public String getPoster() {
        return poster;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrls() {
        return picUrls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(news_id);
        dest.writeInt(user_id);
        dest.writeString(channel_id);
        dest.writeString(homePageTitle);
        dest.writeString(contentPageTitle);
        dest.writeString(pub_time);
        dest.writeString(source);
        dest.writeString(author);
        dest.writeString(poster);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(picUrls);
        dest.writeInt(favorite);
        dest.writeInt(thumbUp);
    }

    @Override
    public String toString() {
        return "News{" +
                "news_id='" + news_id + '\'' +
                "user_id='" + user_id + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", homePageTitle='" + homePageTitle + '\'' +
                ", contentPageTitle='" + contentPageTitle + '\'' +
                ", pub_time='" + pub_time + '\'' +
                ", source='" + source + '\'' +
                ", author='" + author + '\'' +
                ", poster='" + poster + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", picUrls=" + picUrls +
                '}';
    }

    public static String posterMix(final String poster) {
        return "责任编辑:"+poster;
    }
    public static String minPubTime(final String pubTime) { return pubTime.split("\\s+")[0];}
}
