package com.nadavrbn.tindermods;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static de.robv.android.xposed.XposedBridge.hookAllConstructors;

/**
 * Created by Projectants on 09/02/2016.
 */

public class HookMethods implements IXposedHookLoadPackage {

    public static final String PACKAGE_NAME = "com.nadavrbn.tindermods";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("module is working");
        hookAllConstructors(findClass("com.tinder.model.User", loadPackageParam.classLoader), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Date pingTime = (Date)getObjectField(param.thisObject, "mPingTime");
                String bio = (String)getObjectField(param.thisObject, "mBio");

                Context moduleContext = AndroidAppHelper.currentApplication().createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                java.text.DateFormat timeFormatter = DateFormat.getTimeFormat(moduleContext);
                java.text.DateFormat dateFormatter = DateFormat.getDateFormat(moduleContext);

                String timeString = timeFormatter.format(pingTime);

                int dayDifference = daysSinceToday(pingTime);
                String dayString;
                if (dayDifference == 0)
                    dayString = "";
                else if (dayDifference == 1)
                    dayString = "yesterday ";
                else
                    dayString = dateFormatter.format(pingTime) + " ";

                String updateBio = String.format("Last seen %sat %s \n %s", dayString, timeString, bio);

                setObjectField(param.thisObject, "mBio", updateBio);
            }
        });
    }


    private static int daysSinceToday(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        Calendar cal2 = Calendar.getInstance();

        if (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
            int day1 = cal1.get(Calendar.DAY_OF_YEAR);
            int day2 = cal2.get(Calendar.DAY_OF_YEAR);
            if (day1 == day2)
                return 0;
            else if (day2 - day1 == 1)
                return 1;
            return 2;
        }
        return 2;
    }
}
