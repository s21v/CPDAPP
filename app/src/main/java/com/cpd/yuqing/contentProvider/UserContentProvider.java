package com.cpd.yuqing.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.cpd.yuqing.db.dao.UserDao;
import com.cpd.yuqing.db.vo.User;

public class UserContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.s21v.cpdNews.provider";
    private static final int USER_ITEM_TYPE = 1;
    private static final int USER_DIR_TYPE = 2;
    private static final String USER_ITEM_URI_TYPE = "vnd.android.cursor.item/vnd.com.s21v.cpdNews.provider.user";
    private static final String USER_DIR_URI_TYPE = "vnd.android.cursor.dir/vnd.com.s21v.cpdNews.provider.user";
    private static UriMatcher uriMatcher;
//    public static final Uri USER_CHANGE_SIGNAL = Uri.parse("content://"+AUTHORITY+"/user");

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "user", USER_DIR_TYPE);
        uriMatcher.addURI(AUTHORITY, "user/#", USER_ITEM_TYPE);
    }
    private UserDao mUserDao;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result = 0;
        if (!mUserDao.isDBOpen())
            mUserDao.openDB(getContext());
        switch (uriMatcher.match(uri)) {
            case USER_ITEM_TYPE :
                String id = uri.getPathSegments().get(1);
                if(selection != null)
                    result = mUserDao.delete(selection+" id="+id, selectionArgs);
                else
                    result = mUserDao.delete("id=?", new String[]{id});
                break;
            case USER_DIR_TYPE :
                result = mUserDao.delete(selection, selectionArgs);
                break;
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case USER_ITEM_TYPE :
                return USER_ITEM_URI_TYPE;
            case USER_DIR_TYPE :
                return USER_DIR_URI_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (!mUserDao.isDBOpen())
            mUserDao.openDB(getContext());
        if(uriMatcher.match(uri) == USER_DIR_TYPE) {
            User user = new User(values.getAsString("nickname"), values.getAsString("password"), values.getAsString("phoneNum"));
            long id = mUserDao.insert(user);
            Uri resultUri = Uri.withAppendedPath(uri, String.valueOf(id));
            return resultUri;
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        mUserDao = UserDao.getInstance(getContext());
        return true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUserDao.closeDB();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (!mUserDao.isDBOpen())
            mUserDao.openDB(getContext());
        switch(uriMatcher.match(uri)) {
            case USER_DIR_TYPE :
                return mUserDao.query4Cursor(selection, selectionArgs);
            case USER_ITEM_TYPE :
                String id = uri.getPathSegments().get(1);
                if(selection != null)
                    return mUserDao.query4Cursor(selection+" id="+id, selectionArgs);
                else
                    return mUserDao.query4Cursor("id=?", new String[]{id});
            default:
                return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (!mUserDao.isDBOpen())
            mUserDao.openDB(getContext());
        if(uriMatcher.match(uri) == USER_ITEM_TYPE) {
            String id = uri.getPathSegments().get(1);
            if(selection != null)
                return mUserDao.update(values, selection+" id="+id, selectionArgs);
            else
                return mUserDao.update(values, "id=?", new String[]{id});
        }
        return -1;
    }
}
