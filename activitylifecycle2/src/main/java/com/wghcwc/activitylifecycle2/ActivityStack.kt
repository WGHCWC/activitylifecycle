package com.wghcwc.activitylifecycle2

import android.app.Activity
import java.util.*

object ActivityStack {

    private const val TAG = "ActivityStack"
    /**
     * 维护Activity 的list
     */
    private val mActivityList = Collections.synchronizedList(LinkedList<Activity>())

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    fun pushActivity(activity: Activity) {
        mActivityList.add(activity)
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    fun removeActivity(activity: Activity?) {
        mActivityList.remove(activity)
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return if (mActivityList.isEmpty()) null else mActivityList[mActivityList.size - 1]
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    fun finishCurrentActivity() {
        if (mActivityList.isEmpty()) {
            return
        }
        finishActivity(mActivityList[mActivityList.size - 1])
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity) {
        mActivityList.remove(activity)
        activity.finish()
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in mActivityList) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
                break
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        for (activity in mActivityList) {
            activity.finish()
        }
        mActivityList.clear()
    }

}
