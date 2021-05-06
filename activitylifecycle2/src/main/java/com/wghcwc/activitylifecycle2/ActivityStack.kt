package com.wghcwc.activitylifecycle2

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Process
import android.util.Log
import java.util.*
import kotlin.system.exitProcess


/**
 * @author wghcwc
 * @date 21-05-01
 */
object ActivityStack {

    private const val TAG = "ActivityStack"

    private val lock = Object()

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
     * @param cls activity 类名
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in mActivityList) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
                break
            }
        }
    }

    fun getActivityList(): List<Activity> {
        return mActivityList
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

    /**
     * wait until the last activity is stopped.
     *
     * @param timeOutInMillis timeout for wait
     */
    private fun waitForAllActivitiesDestroy(timeOutInMillis: Int=100) {
        synchronized(lock) {
            val start = System.currentTimeMillis()
            var now = start
            while (mActivityList.isNotEmpty() && start + timeOutInMillis > now) {
                try {
                    lock.wait(start - now + timeOutInMillis)
                } catch (ignored: InterruptedException) {
                    Log.w(TAG, "activityStack wait may be error")
                }
                now = System.currentTimeMillis()
            }
            Log.i(TAG, "now killed all activities.")
        }
    }


    /**
     * 崩溃时结束所有activity
     * */
    fun finishAllActivity(uncaughtExceptionThread: Thread?) {
        Log.i(TAG, "Finishing activities prior to killing the Process")
        var wait = false
        for (activity in mActivityList) {
            val isMainThread = uncaughtExceptionThread === activity.mainLooper.thread
            val finisher = Runnable {
                activity.finish()
                Log.d(TAG, "Finished " + activity.javaClass)
            }
            if (isMainThread) {
                finisher.run()
            } else {
                // 崩溃activity不会继续生命周期
                wait = true
                activity.runOnUiThread(finisher)
            }
        }
        if (wait) {
            waitForAllActivitiesDestroy()
        }
        mActivityList.clear()
    }

    /**
     * 关闭所有服务.
     * @param context context
     */
     fun stopServices(context: Context) {
        Log.i(TAG, "Stopping all active services.")
        try {
            val activityManager: ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
            val pid = Process.myPid()
            if (runningServices == null) {
                return
            }
            for (serviceInfo in runningServices) {
                if (serviceInfo.pid == pid) {
                    try {
                        val intent = Intent()
                        intent.component = serviceInfo.service
                        context.stopService(intent)
                    } catch (e: SecurityException) {
                        Log.e(TAG, "Unable to stop Service " + serviceInfo.service.className + ". Permission denied.")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to stop services", e)

        }
    }


    /**
     * 杀掉进程.
     */
    private fun killProcessAndExit() {
        Log.w(TAG, "kill process and exit.")
        val exitType = 10
        Process.killProcess(Process.myPid())
        exitProcess(exitType)
    }


    /**
     * 杀掉服务和进程.
     */
    fun endApplication(context: Context) {
        stopServices(context)
        killProcessAndExit()
    }


}
