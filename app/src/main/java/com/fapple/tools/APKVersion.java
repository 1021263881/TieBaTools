package com.fapple.tools;

import android.content.*;
import android.content.pm.*;

public class APKVersion
{
    /**
     * 获取APP版本号
     */
    public static int getVersionCode(Context mContext) throws mException
	{
        int versionCode = 0;
        try
		{
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        }
		catch (PackageManager.NameNotFoundException e)
		{
            throw new mException("", e.toString());
        }
        return versionCode;
    }

    /**
     * 获取APP版本名
     */
    public static String getVerName(Context context) throws mException
	{
        String versionName = "";
        try
		{
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }
		catch (PackageManager.NameNotFoundException e)
		{
            throw new mException("", e.toString());
        }
        return versionName;
    }
}
