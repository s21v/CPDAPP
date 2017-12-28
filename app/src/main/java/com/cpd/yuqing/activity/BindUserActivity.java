package com.cpd.yuqing.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.cpd.yuqing.CpdnewsApplication;
import com.cpd.yuqing.R;
import com.cpd.yuqing.db.vo.User;
import com.cpd.yuqing.util.NetUtils;
import com.cpd.yuqing.util.OkHttpUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BindUserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BindUserActivity";
    private String platformName;
    private String platformUserID;
    private EditText phoneNumEt;
    private EditText validCodeEt;
    private Button getValidCodeBtn;
    private EventHandler verificationEventHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化SMSSDK, appKey与appSecrect必须与mob平台申请的一致
        SMSSDK.initSDK(this, "1db8beeb19f63", "00ed9695bc864f03ca98a62b5005635f", SMSSDK.InitFlag.DISABLE_CONTACT);
        //获得参数
        if(savedInstanceState != null) {
            platformName = savedInstanceState.getString("platformName");
            platformUserID = savedInstanceState.getString("platformUserID");
        } else {
            platformName = getIntent().getStringExtra("platformName");
            platformUserID = getIntent().getStringExtra("platformUserID");
        }
        //设置UI
        setContentView(R.layout.activity_bind_user);
        phoneNumEt = findViewById(R.id.phoneNum_et);
        validCodeEt = findViewById(R.id.validCode_et);
        getValidCodeBtn = findViewById(R.id.getValidCode_btn);
        Button bindUserBtn = findViewById(R.id.bindUser_bt);
        bindUserBtn.setText(R.string.bindUser);
        //粗略检查是否是手机号：输入是否是11位数字
        phoneNumEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if (!validPhoneNum())
                    showMessage("请输入正确的手机号");
            }
        });
        getValidCodeBtn.setOnClickListener(this);
        bindUserBtn.setOnClickListener(this);
        //初始化监听器
        verificationEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if(result == SMSSDK.RESULT_COMPLETE) {
                    switch (event) {
                        case SMSSDK.EVENT_GET_VERIFICATION_CODE :   //成功获得验证码
                            Log.i(TAG, "SMS事件监听器: 获得验证码成功");
                            break;
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE :    //提交验证码成功
                            HashMap<String,Object> phoneInfo = (HashMap<String, Object>) data;
                            String phoneNum = (String) phoneInfo.get("phone");
                            Log.i(TAG, "SMS事件监听器: 提交验证码成功: "+phoneInfo);
                            //更新远程数据库
                            RequestBody requestBody = null;
                            switch (platformName) {
                                case "QQ" :
                                    requestBody = new FormBody.Builder().add("m", "updateQQByPn")
                                            .add("pn", phoneNum)
                                            .add("pfId", platformUserID)
                                            .build();
                                    break;
                                case "WeChat" :
                                    requestBody = new FormBody.Builder().add("m", "updateWeChatByPn")
                                            .add("pn", phoneNum)
                                            .add("pfId", platformUserID)
                                            .build();
                                    break;
                                case "SinaWeibo" :
                                    requestBody = new FormBody.Builder().add("m", "updateSinaWeiboByPn")
                                            .add("pn", phoneNum)
                                            .add("pfId", platformUserID)
                                            .build();
                                    break;
                            }
                            if(requestBody != null) {
                                Log.i(TAG, "afterEvent: platformUserID"+platformUserID);
                                Request request = new Request.Builder()
                                        .url(NetUtils.UserCommonURL)
                                        .post(requestBody)
                                        .build();
                                OkHttpUtils.Companion.getOkHttpUtilInstance(getApplicationContext())
                                        .httpConnection(request, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.e(TAG, "绑定用户信息失败，网络异常: "+e.getMessage());
                                        showMessage("服务器链接失败");
                                    }
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String status = response.header("status");
                                        final String responseStr = response.body().string();
                                        Log.i(TAG, "onResponse: status:"+status);
                                        if("success".equals(status)) {  //绑定成功
                                            //设置当前用户
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.body().string());
                                                User user = new User(jsonObject.getString("nickname"),
                                                        jsonObject.getString("password"), jsonObject.getString("phoneNum"));
                                                CpdnewsApplication.setCurrentUser(user);
                                                // TODO: 2017/5/17 跳转到主界面
                                            } catch (JSONException e) {
                                                runOnUiThread(() -> showMessage("数据解析失败"));
                                            }
                                        } else {
                                            runOnUiThread(() -> showMessage(responseStr));
                                        }
                                    }
                                });
                            } else {
                                runOnUiThread(() -> showMessage("信息有误，退回到之前界面"));
                                finish();
                            }
                            break;
                    }
                }  else {    //toast显示服务器返回的网络提示
                    Log.e(TAG, "SMS事件监听器: "+((Throwable)data).getMessage());
                    Throwable throwable = (Throwable)data;
                    throwable.printStackTrace();
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
                        final String detail = object.getString("detail"); //错误描述
                        int status = object.getInt("status");//错误代码
                        if (status>0 && !TextUtils.isEmpty(detail))
                            runOnUiThread(() -> showMessage(detail));
                    } catch (JSONException e) {
                        Log.e(TAG, "SMS事件监听器: json解析发生异常"+e.getMessage());
                    }
                }
            }
        };
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("platformUserID", platformUserID);
        outState.putString("platformName", platformName);
    }

    //检查手机号是否填写且是否正确（粗略检查）
    private boolean validPhoneNum() {
        String phoneNum = phoneNumEt.getText().toString();
        if(TextUtils.isEmpty(phoneNum))
            return false;
        else
            return (phoneNum.matches("\\d{11}"));
    }

    //弹出toast
    private void showMessage(String message) {
        Toast.makeText(BindUserActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getValidCode_btn :
                if(!validPhoneNum())
                    showMessage("请输入正确的手机号");
                else {
                    //请求发送验证码短信
                    SMSSDK.getVerificationCode("86", phoneNumEt.getText().toString(), new OnSendMessageHandler() {
                        @Override
                        public boolean onSendMessage(String country, String phone) {
                            return false;   //返回true表示此号码无须实际接收短信
                        }
                    });
                    //禁用按钮一分钟
                    getValidCodeBtn.setEnabled(false);
                    getValidCodeBtn.setText("等待60秒");
                    final ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
                    @SuppressLint("HandlerLeak")
                    final Handler handler = new Handler(){
                        int seconds = 60;
                        @Override
                        public void handleMessage(Message msg) {
                            if (seconds > 1) {
                                seconds--;
                                getValidCodeBtn.setText("等待"+seconds+"秒");

                            } else {
                                singleThreadScheduledExecutor.shutdownNow();
                                getValidCodeBtn.setText(R.string.getValidCode);
                                getValidCodeBtn.setEnabled(true);
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
            case R.id.bindUser_bt :
                // 绑定用户
                // 检查输入
                if (!validPhoneNum()) {
                    showMessage("请输入正确的手机号");
                    break;
                }
                SMSSDK.submitVerificationCode("86", phoneNumEt.getText().toString(), validCodeEt.getText().toString());
        }
    }
}
