package com.cpd.yuqing.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cpd.yuqing.CpdnewsApplication;
import com.cpd.yuqing.R;
import com.cpd.yuqing.db.dao.UserDao;
import com.cpd.yuqing.db.vo.User;
import com.cpd.yuqing.util.NetUtils;
import com.cpd.yuqing.util.OkHttpUtils;
import com.cpd.yuqing.util.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RegisterActivity";
    private EditText phoneNumEt;
    private EditText passwordEt;
    private EditText passwordAgainEt;
    private EditText validCodeEt;
    private Button getValidCodeBt;
    private EventHandler verificationEventHandler;
    private boolean registerWithPlatformId;
    private boolean forgetPassword;
    private String platformName;
    private String platformID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //是否是带有第三方平台信息,有的话保存数据
        if(savedInstanceState != null) {
            registerWithPlatformId = savedInstanceState.getBoolean(LoginActivity.KEY_REGISTER_WITH_PLATFORM_ID, false);
            if (registerWithPlatformId) {
                platformID = savedInstanceState.getString("platformID");
                platformName = savedInstanceState.getString("platformName");
            }
            forgetPassword = savedInstanceState.getBoolean(LoginActivity.KEY_FORGET_PASSWORD, false);
        } else {
            registerWithPlatformId = getIntent().getBooleanExtra(LoginActivity.KEY_REGISTER_WITH_PLATFORM_ID, false);
            if (registerWithPlatformId) {
                platformID = getIntent().getStringExtra("platformID");
                platformName = getIntent().getStringExtra("platformName");
            }
            forgetPassword = getIntent().getBooleanExtra(LoginActivity.KEY_FORGET_PASSWORD, false);
        }
        Log.i(TAG, "onCreate: forgetPassword:"+forgetPassword);
        //加载布局
        setContentView(R.layout.activity_register);
        phoneNumEt = findViewById(R.id.phoneNum_et);
        passwordEt = findViewById(R.id.password_et);
        passwordAgainEt = findViewById(R.id.password_et_again);
        validCodeEt = findViewById(R.id.validCode);
        getValidCodeBt = findViewById(R.id.getValidCode);
        Button registerBt = findViewById(R.id.register_bt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(forgetPassword) {
            toolbar.setTitle("重置密码");
            registerBt.setText("重置密码");
        } else {
            toolbar.setTitle(R.string.register);
            registerBt.setText(R.string.register);
        }
        setSupportActionBar(toolbar);

        //粗略检查是否是手机号：输入是否是11位数字
        phoneNumEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if (!validPhoneNum())
                    showMessage("请输入正确的手机号");
            }
        });
        //检查两次输入的密码是否一致
        passwordAgainEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if (!validPasswordAgain())
                    showMessage("两次输入的密码不一致");
            }
        });
        //检查输入是否输入了密码
        passwordEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(!validPassword())
                    showMessage("请输入密码");
            }
        });

        //按钮注册监听器
        getValidCodeBt.setOnClickListener(this);
        registerBt.setOnClickListener(this);

        //实例化SMS事件监听器
        verificationEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if(result == SMSSDK.RESULT_COMPLETE) {
                    switch (event) {
                        case SMSSDK.EVENT_GET_VERIFICATION_CODE :
                            Log.i(TAG, "SMS事件监听器: 获得验证码成功");
                            boolean smart = (Boolean) data;
                            if (smart) {
                                //通过智能验证
                                Log.i(TAG, "SMS事件监听器: 通过智能验证");
                            } else {
                                //依然走短信验证
                                Log.i(TAG, "SMS事件监听器: 未通过智能验证，依然走短信验证");
                            }
                            break;
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE :
                            HashMap<String,Object> countryPhone = (HashMap<String, Object>) data;
                            Log.i(TAG, "SMS事件监听器: 提交验证码成功: "+countryPhone);
                            final String phoneNum = (String) countryPhone.get("phone");
                            //验证成功，向服务器注册用户
                            RequestBody formEntry;
                            if(forgetPassword) {
                                formEntry = new FormBody.Builder()
                                        .add("m", "updatePassword")
                                        .add("pn", phoneNum)
                                        .add("p", Utils.SHAEncrypt(passwordEt.getText().toString()))
                                        .build();
                            } else {
                                if(!registerWithPlatformId) {
                                    formEntry = new FormBody.Builder()
                                            .add("m", "register")
                                            .add("pn", phoneNum)
                                            .add("p", Utils.SHAEncrypt(passwordEt.getText().toString()))
                                            .build();
                                } else {    //注册并绑定第三方平台信息
                                    formEntry = new FormBody.Builder()
                                            .add("m", "registerWithPlatformInfo")
                                            .add("pn", phoneNum)
                                            .add("p", Utils.SHAEncrypt(passwordEt.getText().toString()))
                                            .add("pfn", platformName)
                                            .add("pfId", platformID)
                                            .build();
                                }
                            }
                            Request request = new Request.Builder()
                                    .url(NetUtils.UserCommonURL)
                                    .post(formEntry)
                                    .build();
                            OkHttpUtils.Companion.getOkHttpUtilInstance(getApplicationContext())
                                    .httpConnection(request, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.i(TAG, "注册用户,数据库写入失败: "+e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.i(TAG, "注册用户,数据库写入成功");
                                    String status = response.header("status");
                                    if("success".equals(status)) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            String nicknameStr = jsonObject.getString("name");
                                            String phoneNumStr = jsonObject.getString("phoneNum");
                                            String passwordValidStr = jsonObject.getString("password");
                                            String passwordStr = passwordEt.getText().toString();
                                            User currentUser = new User(nicknameStr, passwordStr, phoneNumStr);
                                            UserDao userDao = UserDao.getInstance(RegisterActivity.this);
                                            if (Utils.SHAEncrypt(passwordStr).equals(passwordValidStr)) {
                                                if(forgetPassword) {
                                                    //修改数据库中的密码
                                                    userDao.update(currentUser, "phoneNum = ?", new String[]{phoneNum});
                                                } else if(! registerWithPlatformId){
                                                    //将新注册用户的信息写入数据库
                                                    userDao.insert(currentUser);
                                                }
//                                                getContentResolver().notifyChange(UserContentProvider.USER_CHANGE_SIGNAL, null);
                                                userDao.closeDB();
                                                //设置当前用户
                                                CpdnewsApplication.setCurrentUser(currentUser);
                                                // TODO: 2017/5/11 跳转到主界面

                                            }
                                        } catch (JSONException e) {
                                            Log.e(TAG, "解析用户json数据失败: " + e.getMessage());
                                        }
                                    } else {
                                        Log.e(TAG, "数据库操作失败");
                                    }
                                }
                            });
                            break;
                        case SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES :
                            //获得支持发送验证码的国家列表
                            ArrayList<HashMap<String,Object>> countries = (ArrayList<HashMap<String, Object>>) data;
                            for (HashMap country : countries)
                                Log.i(TAG, "SMS事件监听器: 支持发送验证码的国家:"+country);
                            break;
                    }
                } else {    //toast显示服务器返回的网络提示
                    Log.e(TAG, "SMS事件监听器: "+((Throwable)data).getMessage());
                    Throwable throwable = (Throwable)data;
                    throwable.printStackTrace();
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
                        final String detail = object.getString("detail"); //错误描述
                        int status = object.getInt("status");//错误代码
                        if (status>0 && !TextUtils.isEmpty(detail))
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showMessage(detail);
                                }
                            });
                    } catch (JSONException e) {
                        Log.e(TAG, "SMS事件监听器: json解析发生异常"+e.getMessage());
                    }
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LoginActivity.KEY_REGISTER_WITH_PLATFORM_ID, registerWithPlatformId);
        if(registerWithPlatformId) {
            outState.putString("platformID", platformID);
            outState.putString("platformName", platformName);
        }
        outState.putBoolean(LoginActivity.KEY_FORGET_PASSWORD, forgetPassword);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册监听器
        SMSSDK.registerEventHandler(verificationEventHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注销监听器
        SMSSDK.unregisterEventHandler(verificationEventHandler);
    }

    //弹出toast
    private void showMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    //检查手机号是否填写且是否正确（粗略检查）
    private boolean validPhoneNum() {
        String phoneNum = phoneNumEt.getText().toString();
        if(TextUtils.isEmpty(phoneNum))
            return false;
        else
            return (phoneNum.matches("\\d{11}"));
    }

    //检验密码是否输入
    private boolean validPassword() {
        String password = passwordEt.getText().toString();
        if(TextUtils.isEmpty(password))
            return false;
        else
            return true;
    }

    //检验两次密码是否一致
    private boolean validPasswordAgain() {
        if (!validPassword())
            return false;
        else {
            String passwordAgain = passwordAgainEt.getText().toString();
            if (!TextUtils.isEmpty(passwordAgain) && passwordAgain.equals(passwordEt.getText().toString()))
                return true;
            else
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getValidCode :    //点击获得验证码按钮
                //检查手机号是否填写且是否正确（粗略检查）
                if(!validPhoneNum())
                    showMessage("请输入正确的手机号");
                else {
                    //请求发送验证码短信
                    SMSSDK.getVerificationCode("86", phoneNumEt.getText().toString());
                    //禁用按钮一分钟
                    getValidCodeBt.setEnabled(false);
                    getValidCodeBt.setText("等待60秒");
                    final ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
                    @SuppressLint("HandlerLeak")
                    final Handler handler = new Handler(){
                        int seconds = 60;
                        @Override
                        public void handleMessage(Message msg) {
                            if (seconds > 1) {
                                seconds--;
                                getValidCodeBt.setText("等待"+seconds+"秒");

                            } else {
                                   singleThreadScheduledExecutor.shutdownNow();
                                getValidCodeBt.setText(R.string.getValidCode);
                                getValidCodeBt.setEnabled(true);
                            }
                        }
                    };
                    singleThreadScheduledExecutor.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                }
                break;
            case R.id.register_bt : //点击注册按钮
                //检查输入
                if (!validPhoneNum()) {
                    showMessage("请输入正确的手机号");
                    break;
                }
                if (!validPassword()) {
                    showMessage("请输入密码");
                    break;
                }
                if (!validPasswordAgain()) {
                    showMessage("两次密码不一致");
                    break;
                }
                if (!validCode()) {
                    showMessage("请输入短信验证码");
                    break;
                }
                //向Mob服务器提交验证码，验证成功后会通过EventHandler返回国家代码和电话号码，之后再进行注册工作
                SMSSDK.submitVerificationCode("86", phoneNumEt.getText().toString(), validCodeEt.getText().toString());
                break;
            default:
                break;
        }
    }

    //检查验证码是否为空
    private boolean validCode() {
        return !TextUtils.isEmpty(validCodeEt.getText().toString());
    }
}
