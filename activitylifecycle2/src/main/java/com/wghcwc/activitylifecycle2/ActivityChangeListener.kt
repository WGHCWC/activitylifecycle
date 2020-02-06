package com.wghcwc.activitylifecycle2

import android.app.Activity

/**
 * @author wghcwc
 * @date 19-12-5
 */
interface ActivityChangeListener {
    /**
     * 当前Activity Destroy
     *
     * @param activity 当前Activity
     * @return true 删除当前listener
     * false 不变
     */
    fun onActivityDestroy(activity: Activity)

    /**
     * 状态改变
     *
     * @param activity
     * @param state
     */
    fun onActivitySateChange(activity: Activity, state: ActivityState)
}