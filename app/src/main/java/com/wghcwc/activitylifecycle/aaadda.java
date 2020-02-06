package com.wghcwc.activitylifecycle;

import android.app.Activity;

import com.wghcwc.activitylifecycle2.ActivityLifecycle;

/**
 * @author wghcwc
 * @date 20-2-6
 */
public class aaadda extends Activity {


    public void aa(){
        ActivityLifecycle.init(getApplication());
    }

}
