package com.esdk.testvc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements AuthNotify {
    private static final int AUTH_SMC_MSG = 1;
    private Button authBtn;
    private Handler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authBtn = (Button) findViewById(R.id.auth); //注册4.6.2自定义的鉴权回调
        LoginService.getInstance().registerAuthNotify(this);
        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //填入服务器及帐号信息进行鉴权登录
                //LoginService.getInstance().authorize("01051211", "Huawei@123", "172.22.11.228", 5060);
                LoginService.getInstance().authorize("8812346", "Admin@123", "10.10.1.20", 443);
            }
        });

    }


    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AUTH_SMC_MSG:
                    if (msg.arg1 == 0) {
                        Toast.makeText(MainActivity.this, "SMC鉴权登录成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "SMC鉴权失败,reason=" + msg.arg1, Toast.LENGTH_LONG).show();
                    }
                    ((TextView)findViewById(R.id.textResult)).setText("AUTH_SMC_MSG="+msg.arg1);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onAuthSMCResult(int result) {
        Message message = new Message();
        message.what = AUTH_SMC_MSG;
        message.arg1 = result;
        handler.sendMessage(message);
    }

}

