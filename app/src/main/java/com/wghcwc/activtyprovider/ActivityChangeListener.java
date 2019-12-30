package com.wghcwc.activtyprovider;

import android.app.Activity;

/**
 * @author wghcwc
 * @date 19-12-5
 */
public interface ActivityChangeListener {


    /**
     * 当前Activity Destroy
     *
     * @param activity 当前Activity
     * @return true 删除当前listener
     * false 不变
     */
    void onActivityDestroy(Activity activity);

    /**
     * activity 状态改变
     *
     * @param activity activity状态改变
     * @param state    状态{@link ActivityState}
     */
    void onActivitySateChange(Activity activity, ActivityState state);

}
