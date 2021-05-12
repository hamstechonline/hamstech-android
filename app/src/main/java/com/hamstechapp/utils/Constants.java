package com.hamstechapp.utils;

public class Constants {
    public static final boolean DEBUG = Boolean.parseBoolean("true");
    static String BaseUrl = "http://app.hamstech.com/qc/";
    public static String getOTP = BaseUrl+"api/list/getotp/";
    public static String verifyOTP = BaseUrl+"api/list/verifyotp/";
    public static String hamstech_home = BaseUrl+"api/list/hamstech_home";
    public static String getProfile = BaseUrl+"api/list/getprofile/";
    public static String updateprofile = BaseUrl+"api/list/updateprofile/";
    public static String getRequestCallBack = BaseUrl+"api/list/requestcallback/";
    public static String getNotifications = BaseUrl+"api/list/get_notification";
    public static String uploadImage = BaseUrl+"api/list/updateimage/";
    public static String post_createhocorder = "https://wwww.hunarcourses.com/api/createhocorder.php";
    public static String create_hoconlineorder = "https://www.hunarcourses.com/api/createhoconlineorder.php";
    public static String getAboutData = BaseUrl+"api/list/aboutus";
    public static String getGcmid = BaseUrl+"api/list/updategcm/";
    public static String getOnBoarding = BaseUrl+"api/list/boardingpage";
    public static String getLogevent = BaseUrl+"api/list/logevent";
    public static String getRsaKey = "https://app.hamstech.com/dev/rsa-handling-ccavenue/GetRSA.php?ORDER_ID=";
    public static String save_like_dislike = BaseUrl+"api/list/save_like_dislike";
    public static String getversion = BaseUrl+"api/list/getversion";
    public static String getaffliations = BaseUrl+"api/list/affliations";
    public static String getmentors = BaseUrl+"api/list/mentors";
    public static String lifeatHamstech = BaseUrl+"api/list/life_at_hamstech";
    public static String lifeatHamstechEvents = BaseUrl+"api/list/life_at_hamstech_events";
    public static String getCourseData = BaseUrl+"api/list/get_careerooption_bycat/";
    public static String redirectUrl = "https://app.hamstech.com/dev/api/list/transactions/";
    public static String getCourseDetails = BaseUrl+"api/list/get_coursecurriculum_bycat/";
    public static String getSearch = BaseUrl+"api/list/get_search/";
    public static String getCounselling = BaseUrl+"api/list/counseling_page";
    public static String init_transactions = BaseUrl+"api/list/init_transactions";
}
