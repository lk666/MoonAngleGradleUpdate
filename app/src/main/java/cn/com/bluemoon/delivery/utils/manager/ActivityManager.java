package cn.com.bluemoon.delivery.utils.manager;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;

public class ActivityManager {
	private static ActivityManager instance;
	private Stack<Activity> activityStack;// activityջ

	private ActivityManager() {
	}


	public static ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	public void pushOneActivity(Activity actvity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(actvity);
		Log.d("MyActivityManager ", "size = " + activityStack.size());
	}


	public Activity getLastActivity() {
		return activityStack.lastElement();
	}


	public void popOneActivity(Activity activity) {
		if (activityStack != null && activityStack.size() > 0) {
			if (activity != null) {
				activity.finish();
				activityStack.remove(activity);
			}
		}
	}

	public void finishAllActivity() {
		if (activityStack != null) {
			while (activityStack.size() > 0) {
				Activity activity = getLastActivity();
				if (activity == null)
					break;
				popOneActivity(activity);
			}
		}
	}
}
