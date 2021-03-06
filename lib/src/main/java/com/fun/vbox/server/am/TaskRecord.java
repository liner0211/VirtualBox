package com.fun.vbox.server.am;

import android.content.ComponentName;
import android.content.Intent;

import com.fun.vbox.remote.AppTaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lody
 */

class TaskRecord {
    public final List<ActivityRecord> activities = new ArrayList<>();
    public int taskId;
    public int userId;
    public String affinity;
    public Intent taskRoot;

    TaskRecord(int taskId, int userId, String affinity, Intent intent) {
        this.taskId = taskId;
        this.userId = userId;
        this.affinity = affinity;
        this.taskRoot = intent;
    }

    ActivityRecord getRootActivityRecord() {
        synchronized (activities) {
            for (int i = 0; i < activities.size(); i++) {
                final ActivityRecord r = activities.get(i);
                if (r.marked) {
                    continue;
                }
                return r;
            }
        }
        return null;
    }

    public ActivityRecord getTopActivityRecord() {
        return getTopActivityRecord(false);
    }

    public ActivityRecord getTopActivityRecord(boolean containFinishedActivity) {
        synchronized (activities) {
            if (activities.isEmpty()) {
                return null;
            }
            for (int i = activities.size() - 1; i >= 0; i--) {
                ActivityRecord r = activities.get(i);
                if (containFinishedActivity || !r.marked) {
                    return r;
                }
            }
            return null;
        }
    }

    AppTaskInfo getAppTaskInfo() {
        int len = activities.size();
        if (len <= 0) {
            return null;
        }
        ComponentName top = activities.get(len - 1).component;
        return new AppTaskInfo(taskId, taskRoot, taskRoot.getComponent(), top);
    }

    public boolean isFinishing() {
        boolean allFinish = true;
        for (ActivityRecord r : activities) {
            if (!r.marked) allFinish = false;
        }
        return allFinish;
    }

    public void finish() {
        synchronized (activities) {
            for (ActivityRecord r : activities) {
                r.marked = true;
            }
        }
    }
}
