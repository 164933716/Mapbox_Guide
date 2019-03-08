package com.mapbox.guide;

import com.ksy.ui.BaseApp;

public class App extends BaseApp {

    protected static App context;
    public static String globalToken = "sk.eyJ1IjoiMTY0OTMzNzE2IiwiYSI6ImNqc2s0NmhrcjJsczkzeW82dTM3cmQybGgifQ.cYXe_Du26evYatvBNmsKdQ";
    public static String chinaToken = "pk.eyJ1IjoiYmR4MjciLCJhIjoiY2prZHJ1NHJtMzNvcDNxa3lnczMyaW5jcCJ9.Srw0di6BdZdG_-yaw-IouQ";
    public static String mapToken = "pk.eyJ1IjoiYmR4MjciLCJhIjoiY2prZHJ1NHJtMzNvcDNxa3lnczMyaW5jcCJ9.Srw0di6BdZdG_-yaw-IouQ";
    //    public static String toolToken = "sk.eyJ1IjoiMTY0OTMzNzE2IiwiYSI6ImNqcHV0cHF4ZzBkaGYzeG1tNnViN2t4emkifQ.cvnw6Tb1p8d0qLakzVgJHQ";
    public static String toolToken = "sk.eyJ1Ijoibm9haHh1IiwiYSI6ImNqcW0xOGRmOTBzbDk0MnMxdmgxNWE0cjIifQ.muFTyK3xueum0V4iaKfEPQ";
    public static String apksToken = "pk.eyJ1IjoiZWxlY3Ryb3N0YXQtdGVzdCIsImEiOiJjamRhaHBhejkydXhlMnhvNmZhZTk3cjI1In0.9I3NYZF29F-XQHW1JIzIPg";

    public static App getInstance() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
