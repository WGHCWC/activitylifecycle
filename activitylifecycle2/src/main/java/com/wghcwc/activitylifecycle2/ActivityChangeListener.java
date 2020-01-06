package com.wghcwc.activitylifecycle2;

import android.app.Activity;

/**
 * @author wghcwc
 * @date 19-12-5
 */
public interface ActivityChangeListener {


    /**
     * 当前Activity Destroy
     * @return true 删除当前listener
     * false 不变
     *
     * @param activity 当前Activity
     *
     */
    void onActivityDestroy(Activity activity) ;

    void onActivitySateChange(Activity activity, ActivityState state);

}
