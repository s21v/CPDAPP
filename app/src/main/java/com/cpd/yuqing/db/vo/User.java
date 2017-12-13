package com.cpd.yuqing.db.vo;

/**
 * Created by s21v on 2017/4/20.
 */
public class User {
    private static final String TAG = "User";
    int id;
    String nickname;
    String password;
    String phoneNum;

    public User() {
        super();
    }

    public User(int id, String nickname, String password, String phoneNum) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public User(String nickname, String password, String phoneNum) {
        this.nickname = nickname;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
