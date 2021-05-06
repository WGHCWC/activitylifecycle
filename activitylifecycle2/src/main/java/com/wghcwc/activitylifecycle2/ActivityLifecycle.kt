package com.wghcwc.activitylifecycle2

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import java.util.*

/**
 * @author wghcwc
 * @date 19-12-5
 */
object ActivityLifecycle : ActivityLifecycleCallbacks {
    private val activityLinked = WeakHashMap<Activity, LinkedList<ActivityChangeListener>>()
    private lateinit var app: Application
    private var init = false

    //前台activity数量
    private var foregroundCount = 0

    //activity config改变数量
    private var configChangeCount = 0

    //是否前台数据
    @Volatile
    private var isForeground = true


    fun init(application: Application) {
        if (init) {
            return
        }
        application.registerActivityLifecycleCallbacks(this)
        app = application
        init = true
    }

    fun getApp(): Application {
        if (!init) {
            throw NullPointerException(" need ActivityLifecycle.init(Application application) in Application")
        }
        return app
    }

    /**
     * 添加activity监听
     * */
    fun add(activity: Activity, listener: ActivityChangeListener?) {
        var linkedList = activityLinked[activity]
        if (linkedList == null) {
            linkedList = LinkedList()
        }
        linkedList.push(listener)
        activityLinked[activity] = linkedList
    }

    /**
     * 移除activity指定监听
     * */
    fun remove(activity: Activity?, listener: ActivityChangeListener) {
        val linkedList: LinkedList<ActivityChangeListener>? = activityLinked[activity]
        if (linkedList != null) {
            linkedList.remove(listener)
            if (linkedList.size == 0) {
                activityLinked.remove(activity)
            }
        }
    }

    /**
     * 移除activity所有监听
     * */
    fun remove(activity: Activity?) {
        activityLinked.remove(activity)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityStack.pushActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {

        if (foregroundCount <= 0) {
            isForeground = true
        }
        if (configChangeCount < 0) {
            configChangeCount++
        } else {
            foregroundCount++
        }
        val linkedList = activityLinked[activity] ?: return
        for (changeListener in linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.STARTED)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        val linkedList = activityLinked[activity] ?: return
        for (changeListener in linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.RESUMED)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        val linkedList = activityLinked[activity] ?: return
        for (changeListener in linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.PAUSED)
        }
    }

    override fun onActivityStopped(activity: Activity) {

        if (activity.isChangingConfigurations) {
            configChangeCount--
        } else {
            foregroundCount--
            if (foregroundCount <= 0) {
                isForeground = false
            }
        }
        val linkedList = activityLinked[activity] ?: return
        for (changeListener in linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.STOPPED)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }



    override fun onActivityDestroyed(activity: Activity) {
        ActivityStack.removeActivity(activity)
        val linkedList = activityLinked[activity] ?: return
        for (changeListener in linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.DESTROYED)
            changeListener.onActivityDestroy(activity)
        }
        remove(activity)
    }


    /**
     * 当前应用是否在前台
     * */
    fun isForeground(): Boolean {
        return isForeground
    }
}