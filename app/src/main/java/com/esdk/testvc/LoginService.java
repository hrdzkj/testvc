package com.esdk.testvc;

import android.os.Environment;
import android.util.Log;

import com.huawei.tup.TUPInterfaceService;
import com.huawei.tup.login.LoginAuthInfo;
import com.huawei.tup.login.LoginAuthServerInfo;
import com.huawei.tup.login.LoginAuthType;
import com.huawei.tup.login.LoginAuthorizeParam;
import com.huawei.tup.login.LoginLogLevel;
import com.huawei.tup.login.LoginServerType;
import com.huawei.tup.login.LoginSingleServerInfo;
import com.huawei.tup.login.LoginSmcAuthorizeResult;
import com.huawei.tup.login.LoginVerifyMode;
import com.huawei.tup.login.sdk.TupLoginErrorID;
import com.huawei.tup.login.sdk.TupLoginManager;
import com.huawei.tup.login.sdk.TupLoginNotifyBase;
import com.huawei.tup.login.sdk.TupLoginOptResult;

import java.io.File;
import java.util.List;

public class LoginService extends TupLoginNotifyBase {
    private static LoginService ins;
    private TupLoginManager tupLoginManager;
    private AuthNotify authNotify;

    private LoginService() {
    }

    public synchronized static LoginService getInstance() {
        if (ins == null) {
            ins = new LoginService();
        }
        return ins;
    }

    public void registerAuthNotify(AuthNotify notify) {
        authNotify = notify;
    }

    public int init(TUPInterfaceService tupService) {
       //实例TupLoginManager
        tupLoginManager = TupLoginManager.getIns(this, VCApplication.getApplication());
      // 设置日志参数
        String path = Environment.getExternalStorageDirectory() + File.separator + "VCTEST";
        int fileCount = 1;
        int maxLogSize = 5120;
        tupLoginManager.setLogParam(LoginLogLevel.LOGIN_E_LOG_INFO, maxLogSize, fileCount, path);

        // init login
        // tupLoginManager.setCertPath(""); liuyi 屏蔽
        tupLoginManager.setVerifyMode(LoginVerifyMode.LOGIN_E_VERIFY_MODE_NONE); //设置鉴权模式
         int ret =tupLoginManager.loginInit(tupService); //登录初始化
        if (ret != 0)
        {
            Log.e("liuyi", "login init is failed,ret="+ret);
        }
        return ret;
    }

    public int unInit() {
        return tupLoginManager.loginUninit();
    }

    public int authorize(String userName, String password, String serverUrl, int serverPort) {
        LoginAuthInfo authInfo = new LoginAuthInfo();
        authInfo.setUserName(userName);
        authInfo.setPassword(password);
        LoginAuthServerInfo serverInfo = new LoginAuthServerInfo();
        serverInfo.setServerType(LoginServerType.LOGIN_E_SERVER_TYPE_SMC);
        serverInfo.setServerUrl(serverUrl);
        serverInfo.setServerPort(serverPort);
        //serverInfo.setServerVersion(""); //必须
        serverInfo.setServerVersion("V6R6C00");
        serverInfo.setProxyUrl(serverUrl);
        serverInfo.setProxyPort(serverPort);
        LoginAuthorizeParam authorizeParam = new LoginAuthorizeParam();
        authorizeParam.setUserId(1);// liuyi add
        authorizeParam.setAuthType(LoginAuthType.LOGIN_E_AUTH_NORMAL);//liuyi add
        authorizeParam.setAuthInfo(authInfo);
        authorizeParam.setAuthServer(serverInfo);
        //authorizeParam.setUserAgent("TUP VC Mobile");
        authorizeParam.setUserAgent("Huawei TE Mobile");// liuyi modify
        authorizeParam.setUserTiket(""); //必须
        int result = tupLoginManager.authorize(authorizeParam);
        return result;
    }

    @Override
    public void onAuthorizeResult(int i, TupLoginOptResult tupLoginOptResult, LoginSmcAuthorizeResult loginSmcAuthorizeResult) {
        int result;
        if (tupLoginOptResult == null) {
            Log.e("LoginService", "----tupLoginOptResult is null");
            result = -1;
        } else {
            int loginResult = tupLoginOptResult.getOptResult();
            if (loginResult != TupLoginErrorID.LOGIN_E_ERR_SUCCESS) {
                Log.e("LoginService", "----login failed,result=" + loginResult);
                result = loginResult;
            } else {
                Log.i("LoginService", "----login success"); //此时可以去获取鉴权信息,以SMC信息为例
                List<LoginSingleServerInfo> smc_servers = loginSmcAuthorizeResult.getSmcServers();
                for (LoginSingleServerInfo serverInfo : smc_servers) {
                    Log.i("LoginService", "-------server_uri:" + serverInfo.getServerUri());
                }
                result = 0;
            }
        }
        authNotify.onAuthSMCResult(result);
    }
}