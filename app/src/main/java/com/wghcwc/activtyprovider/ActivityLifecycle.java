package com.wghcwc.activtyprovider;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.WeakHashMap;

/**
 * @author wghcwc
 * @date 19-12-5
 */
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    public static ActivityLifecycle instance;

    private final WeakHashMap<Activity, LinkedList<ActivityChangeListener>> activityLinked;

    static {
        instance = new ActivityLifecycle();
    }

    private ActivityLifecycle() {
        activityLinked = new WeakHashMap<>();
    }

    public static ActivityLifecycle getInstance() {
        return instance;
    }

    public void add(Activity activity, ActivityChangeListener listener) {
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList == null) {
            linkedList = new LinkedList<>();
        }
        linkedList.push(listener);
        activityLinked.put(activity, linkedList);

    }

    public void remove(Activity activity, ActivityChangeListener listener) {
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList != null) {
            linkedList.remove(listener);
            if (linkedList.size() == 0) {
                activityLinked.remove(activity);
            }
        }
    }

    public void remove(Activity activity) {
        activityLinked.remove(activity);

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityStack.pushActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList == null) return;
        for (ActivityChangeListener changeListener : linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.STARTED);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList == null) return;
        for (ActivityChangeListener changeListener : linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.RESUMED);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList == null) return;
        for (ActivityChangeListener changeListener : linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.PAUSED);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList == null) return;
        for (ActivityChangeListener changeListener : linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.STOPPED);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityStack.removeActivity(activity);
        LinkedList<ActivityChangeListener> linkedList = activityLinked.get(activity);
        if (linkedList == null) return;
        for (ActivityChangeListener changeListener : linkedList) {
            changeListener.onActivitySateChange(activity, ActivityState.DESTROYED);
            changeListener.onActivityDestroy(activity);
        }
        remove(activity);
    }
}
