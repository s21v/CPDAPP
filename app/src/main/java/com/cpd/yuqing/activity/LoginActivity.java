package com.cpd.yuqing.activity;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpd.yuqing.CpdnewsApplication;
import com.cpd.yuqing.R;
import com.cpd.yuqing.contentProvider.UserContentProvider;
import com.cpd.yuqing.db.dao.UserDao;
import com.cpd.yuqing.db.vo.User;
import com.cpd.yuqing.util.NetUtils;
import com.cpd.yuqing.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.cpd.yuqing.util.OkHttpUtils;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, View.OnClickListener {
    public static final String KEY_REGISTER_WITH_PLATFORM_ID = "registerWithPlatformId";
    public static final String KEY_FORGET_PASSWORD = "forgetPassword";
    private static final String TAG = "LoginActivity";
    private static final String IS_REMEMBER_PASSWORD = "isRememberPassword";
    private SharedPreferences mSharedPreferences;
    // UI references.
    private AutoCompleteTextView mPhoneNumView;
    private EditText mPasswordView;
    private SimpleCursorAdapter adapter;
    private ImageView passwordCheckBoxIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);
        //初始化UI组件
        //第三方登录
        ImageButton signInByQQ = findViewById(R.id.sign_in_qq);
        ImageButton signInByWeChat = findViewById(R.id.sign_in_wechat);
        ImageButton signInBySinaWeibo = findViewById(R.id.sign_in_sinaweibo);
        signInByQQ.setOnClickListener(this);
        signInByWeChat.setOnClickListener(this);
        signInBySinaWeibo.setOnClickListener(this);
        //手机号和密码输入栏
        mPhoneNumView = findViewById(R.id.username_et);
        mPhoneNumView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = adapter.getCursor();
            if (cursor != null) {
                if (cursor.moveToPosition(position)) {
                    mPhoneNumView.setText(cursor.getString(cursor.getColumnIndex("phoneNum")));
                    mPasswordView.setText(cursor.getString(cursor.getColumnIndex("password")));
                }
            }
        });
        mPasswordView = findViewById(R.id.password_et);
        //记住密码
        passwordCheckBoxIv = findViewById(R.id.pw_checkbox_iv);
        TextView passwordCheckBoxTv = findViewById(R.id.pw_checkbox_tv);
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        boolean isRememberPassword = mSharedPreferences.getBoolean(IS_REMEMBER_PASSWORD, false);
        if (isRememberPassword)
            passwordCheckBoxIv.setImageResource(R.drawable.remember_password_checkbox_checked);
        else
            passwordCheckBoxIv.setImageResource(R.drawable.remember_password_checkbox);
        passwordCheckBoxIv.setOnClickListener(this);
        passwordCheckBoxTv.setOnClickListener(this);
        //登录按钮
        Button signInButton = findViewById(R.id.sign_in_bt);
        signInButton.setOnClickListener(this);
        //忘记密码和注册
        TextView forgetPasswordTv = findViewById(R.id.forget_password_tv);
        TextView registerTv = findViewById(R.id.register_tv);
        forgetPasswordTv.setOnClickListener(this);
        registerTv.setOnClickListener(this);

//        //下拉列表的adapter
//        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, null,
//                new String[]{"phoneNum"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //加载loader
        getLoaderManager().initLoader(0, null, this);
        //没有找到保存头像，使用默认头像
        ImageView logoIv = findViewById(R.id.logo);
        Bitmap defaultLogo = BitmapFactory.decodeResource(getResources(), R.drawable.cpd_logo);
        logoIv.setImageBitmap(Utils.getRoundBitmap(defaultLogo, this, 120));
        //设置虚化背景
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.demo4);
        Utils.gaussianBlur(this, source);
        findViewById(R.id.loginActivity_LinearLayout).setBackground(new BitmapDrawable(source));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //android6.0以上的系统设置通明的系统状态栏和导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            //清除系统提供的默认保护色
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置系统UI的显示方式
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //添加属性可以自定义设置系统工具栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击记住密码
            case R.id.pw_checkbox_iv:
            case R.id.pw_checkbox_tv:
                //转换
                boolean isRememberPassword = mSharedPreferences.getBoolean(IS_REMEMBER_PASSWORD, false);
                mSharedPreferences.edit().putBoolean(IS_REMEMBER_PASSWORD, !isRememberPassword).apply();
                if (!isRememberPassword)
                    passwordCheckBoxIv.setImageResource(R.drawable.remember_password_checkbox_checked);
                else
                    passwordCheckBoxIv.setImageResource(R.drawable.remember_password_checkbox);
                break;
            //点击登录按钮,将用户输入传递给服务器查询，处理返回的结果
            case R.id.sign_in_bt:
                RequestBody requestBody = new FormBody.Builder()
                        .add("m", "query")
                        .add("pn", mPhoneNumView.getText().toString())
                        .add("p", Utils.SHAEncrypt(mPasswordView.getText().toString()))
                        .build();
                Request request = new Request.Builder()
                        .url(NetUtils.UserCommonURL)
                        .post(requestBody)
                        .build();
                OkHttpUtils.Companion.getOkHttpUtilInstance(getApplicationContext())
                        .httpConnection(request, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e(TAG, e.getMessage());
                                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String status = response.header("status");
                                if ("success".equals(status)) {
                                    try {
                                        //获得返回的用户数据
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        String phoneNumStr = jsonObject.getString("phoneNum");
                                        String nicknameStr = jsonObject.getString("name");
                                        String passwordValid = jsonObject.getString("password");
                                        //检查输入正确的用户名是否保存在本地数据库中
                                        if (mSharedPreferences.getBoolean(IS_REMEMBER_PASSWORD, false)) {
                                            UserDao userDao = UserDao.getInstance(LoginActivity.this);
                                            String passwordStr = mPasswordView.getText().toString();
                                            if (Utils.SHAEncrypt(passwordStr).equals(passwordValid)) {
                                                Cursor cursor = userDao.query4Cursor("phoneNum=? and password=?", new String[]{phoneNumStr, passwordStr});
                                                //若没有保存，在本地数据库保存正确的用户名和密码
                                                if (!cursor.moveToNext()) {
                                                    userDao.insert(new User(nicknameStr, passwordStr, phoneNumStr));
                                                }
                                                cursor.close();
                                            }
                                            userDao.closeDB();
                                        }
                                        //设置当前用户
                                        CpdnewsApplication.setCurrentUser(new User(nicknameStr, phoneNumStr, passwordValid));
                                    } catch (JSONException e) {
                                        Log.e(TAG, "解析用户json数据失败: " + e.getMessage());
                                    }
                                    //跳转到主界面
                                    Log.i(TAG, "用户名和密码登录成功");
                                    if (getSharedPreferences("contentSetting", MODE_PRIVATE).getBoolean("isNightMode", false)) {
                                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    }
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else if ("fail".equals(status)) {
                                    //数据库中没有此用户名和密码
                                    runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                                            "用户名/密码 错误", Toast.LENGTH_SHORT).show());
                                }
                            }
                        });
                break;
            //点击忘记密码，跳转到忘记密码页面
            case R.id.forget_password_tv:
                Intent intent2 = new Intent(this, RegisterActivity.class);
                intent2.putExtra(KEY_FORGET_PASSWORD, true);
                startActivity(intent2);
                break;
            //点击注册页面，跳转到注册页面
            case R.id.register_tv:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra(KEY_REGISTER_WITH_PLATFORM_ID, false);
                startActivity(intent);
                break;
            //第三方登录
            case R.id.sign_in_sinaweibo:
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                weibo.SSOSetting(false);    //使用SSO授权方式
                weibo.setPlatformActionListener(new MyPlatformActionListener("SinaWeibo"));
                weibo.showUser(null);
                break;
            case R.id.sign_in_qq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.SSOSetting(false);
                qq.setPlatformActionListener(new MyPlatformActionListener("QQ"));
                qq.showUser(null);
                break;
            case R.id.sign_in_wechat:
                final Platform weChat = ShareSDK.getPlatform(Wechat.NAME);
                weChat.SSOSetting(false);
                weChat.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Log.i(TAG, "微信授权成功: ");
                        Log.i(TAG, "onComplete: platform:" + platform.getDb().exportData());
                        Set<HashMap.Entry<String, Object>> set = hashMap.entrySet();
                        for (HashMap.Entry entry : set)
                            Log.i(TAG, "onComplete: Entry k:" + entry.getKey() + ", v:" + entry.getValue());
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        Log.e(TAG, "onError: ", throwable);
                        weChat.removeAccount(true);
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                weChat.showUser(null);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() called with: i = [" + i + "], bundle = [" + bundle + "]");
        //加载所有已存储的用户名和密码
        Uri uri = Uri.parse("content://" + UserContentProvider.AUTHORITY + "/user");
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished() called with: cursorLoader = [" + cursorLoader + "], cursor = [" + cursor + "]");
//        adapter.swapCursor(cursor);
        //下拉列表的adapter
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_dropdown_item_1line, cursor,
                new String[]{"phoneNum"}, new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mPhoneNumView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset() called with: cursorLoader = [" + cursorLoader + "]");
//        adapter.swapCursor(null);
        mPhoneNumView.setAdapter(null);
    }

    //第三方平台授权监听器
    class MyPlatformActionListener implements PlatformActionListener {
        private String platformName;
        private static final String TAG = "PlatformActionListener";

        MyPlatformActionListener(String platformName) {
            this.platformName = platformName;
        }

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.i(TAG, platformName + "onComplete: ");
            final String platformUserID = platform.getDb().get("userID");
            //查询服务器数据库，服务端判定用户是已注册用户
            RequestBody requestBody = new FormBody.Builder()
                    .add("m", "queryByPlatformId")
                    .add("pId", platformUserID)
                    .add("pName", platformName)
                    .build();
            Request request = new Request.Builder()
                    .url(NetUtils.UserCommonURL)
                    .post(requestBody)
                    .build();
            OkHttpUtils.Companion.getOkHttpUtilInstance(getApplicationContext())
                    .httpConnection(request, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "网络连接失败 onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String status = response.header("status");
                            if ("success".equals(status)) { //网络返回成功，服务中找到对应的账号
                                //设置当前用户
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    User user = new User(jsonObject.getString("name"), jsonObject.getString("password"), jsonObject.getString("phoneNum"));
                                    CpdnewsApplication.setCurrentUser(user);
                                    Log.i(TAG, "onResponse: 转到主页面");
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } catch (JSONException e) {
                                    Log.e(TAG, "解析返回的Json数据失败 onResponse: " + e.getMessage());
                                }
                            } else if ("fail".equals(status)) { //此平台信息没有被记录过
                                runOnUiThread(() -> {
                                    Log.i(TAG, "run: platformUserID:" + platformUserID);
                                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).setMessage("没有找到绑定的用户")
                                            .setPositiveButton("已有用户，去绑定", (dialog12, which) -> {
                                                Intent intent1 = new Intent(LoginActivity.this, BindUserActivity.class);
                                                intent1.putExtra("platformName", platformName);
                                                intent1.putExtra("platformID", platformUserID);
                                                startActivity(intent1);
                                            }).setNegativeButton("没有用户，去注册", (dialog1, which) -> {
                                                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                                                intent1.putExtra(KEY_REGISTER_WITH_PLATFORM_ID, true);
                                                intent1.putExtra("platformName", platformName);
                                                intent1.putExtra("platformID", platformUserID);
                                                startActivity(intent1);
                                            }).create();
                                    dialog.show();
                                });
                            }
                        }
                    });
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e(TAG, platformName + "onError: " + throwable.getMessage());
            platform.removeAccount(true);
        }

        @Override
        public void onCancel(Platform platform, int i) {

        }
    }
}

