package com.cpd.yuqing;

import android.app.Application;
import com.cpd.yuqing.db.vo.User;

import io.vov.vitamio.Vitamio;

/**
 * Created by s21v on 2017/5/15.
 */
public class CpdnewsApplication extends Application {
    private static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CpdnewsApplication.currentUser = currentUser;
    }
}
