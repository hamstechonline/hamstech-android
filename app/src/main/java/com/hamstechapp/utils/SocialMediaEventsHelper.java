package com.hamstechapp.utils;

import android.content.Context;
import android.os.Bundle;

import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class SocialMediaEventsHelper {

    Context context;
    Map<String,Object> eventData = new HashMap<>();
    AppEventsLogger logger;
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle params;

    public SocialMediaEventsHelper(Context context) {
        this.context = context;
        params = new Bundle();
        logger = AppEventsLogger.newLogger(context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        eventData.put(SocialMediaEventsParameters.KEY_USER_NAME,UserDataConstants.userName);
        eventData.put(SocialMediaEventsParameters.KEY_MOBILE_NUMBER,UserDataConstants.userMobile);
        params.putString(SocialMediaEventsParameters.KEY_USER_NAME,UserDataConstants.userName);
        params.putString(SocialMediaEventsParameters.KEY_MOBILE_NUMBER,UserDataConstants.userMobile);
    }

    public void EventContact(){
        logger.logEvent(AppEventsConstants.EVENT_NAME_CONTACT,params);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS);
        mFirebaseAnalytics.logEvent(SocialMediaEventsParameters.EVENT_CONTACTUS, params);
        AppsFlyerLib.getInstance().trackEvent(context, SocialMediaEventsParameters.EVENT_CONTACTUS,eventData);
    }
    public void EventRegisterCourse() {
        logger.logEvent(SocialMediaEventsParameters.EVENT_REGISTERCOURSE, params);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS);
        mFirebaseAnalytics.logEvent(SocialMediaEventsParameters.EVENT_REGISTERCOURSE, params);
        AppsFlyerLib.getInstance().trackEvent(context, SocialMediaEventsParameters.EVENT_REGISTERCOURSE,eventData);
    }
    public void EventCourseWatched(String courseName) {
        params.putString(SocialMediaEventsParameters.KEY_COURSE_NAME,courseName);
        eventData.put(SocialMediaEventsParameters.KEY_COURSE_NAME,courseName);
        logger.logEvent(SocialMediaEventsParameters.EVENT_COURSES_WATCHED, params);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS);
        mFirebaseAnalytics.logEvent(SocialMediaEventsParameters.EVENT_COURSES_WATCHED, params);
        AppsFlyerLib.getInstance().trackEvent(context, SocialMediaEventsParameters.EVENT_COURSES_WATCHED,eventData);
    }
    public void EventAccordion(String EventName){
        params.putString(SocialMediaEventsParameters.KEY_ACCORDION_NAME,EventName);
        eventData.put(SocialMediaEventsParameters.KEY_ACCORDION_NAME,EventName);
        logger.logEvent(SocialMediaEventsParameters.EVENT_ACCORDION, params);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SPENT_CREDITS);
        mFirebaseAnalytics.logEvent(SocialMediaEventsParameters.EVENT_ACCORDION, params);
        AppsFlyerLib.getInstance().trackEvent(context, SocialMediaEventsParameters.EVENT_ACCORDION,eventData);
    }
}
