package com.esdk.testvc;

import android.app.Application;

import com.huawei.tup.TUPInterfaceService;

public class VCApplication extends Application
{
    private static Application app;
    public static Application getApplication()
    {
        return app;
    }
    private void setApplication(Application application)
    {
        app = application;
    }
    private TUPInterfaceService tupInterfaceService;
    @Override
    public void onCreate()
    {
        super.onCreate();
        setApplication(this);
        tupInterfaceService = new TUPInterfaceService();
        tupInterfaceService.StartUpService();
        //基础组件初始化
        String path =getApplicationInfo().dataDir + "/lib";
        tupInterfaceService.SetAppPath(path);

        //LoginService init接口实现参考4.6.3
        LoginService.getInstance().init(tupInterfaceService); //鉴权登录初始化
    }
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        if (tupInterfaceService != null)
        {
            tupInterfaceService.ShutDownService();
        } }
}
