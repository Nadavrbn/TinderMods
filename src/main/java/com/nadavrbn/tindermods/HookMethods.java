package com.nadavrbn.tindermods;

import java.text.SimpleDateFormat;
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
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("module is working");
        hookAllConstructors(findClass("com.tinder.model.User", loadPackageParam.classLoader), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("entered tinder user object");
                Date pingTime = (Date)getObjectField(param.thisObject, "mPingTime");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                String pingString = sdf.format(pingTime);
                String bio = (String)getObjectField(param.thisObject, "mBio");
                StringBuilder builder = new StringBuilder("Last seen at: ");
                builder.append(pingString);
                builder.append(" \n");
                builder.append(bio);
                setObjectField(param.thisObject, "mBio", builder.toString());
/*
                String mName = (String)getObjectField(param.thisObject, "mName");
                String mFacebookId = (String)getObjectField(param.thisObject, "mFacebookId");

                setObjectField(param.thisObject, "mName",mFacebookId);*/
            }
        });
    }
}
