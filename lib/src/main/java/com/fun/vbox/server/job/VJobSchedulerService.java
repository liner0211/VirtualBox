package com.fun.vbox.server.job;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.TextUtils;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.ipc.VJobScheduler;
import com.fun.vbox.client.stub.StubManifest;
import com.fun.vbox.helper.utils.Singleton;
import com.fun.vbox.os.VEnvironment;
import com.fun.vbox.remote.VJobWorkItem;
import com.fun.vbox.server.interfaces.IJobService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Lody
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VJobSchedulerService extends IJobService.Stub {

    private static final String TAG = VJobScheduler.class.getSimpleName();

    private static final int JOB_FILE_VERSION = 1;
    private final Map<JobId, JobConfig> mJobStore = new HashMap<>();
    private int mNextJobId = 1;

    private final JobScheduler mScheduler = (JobScheduler)
            VCore.get().getContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);

    private final ComponentName mJobProxyComponent;

    private VJobSchedulerService() {
        mJobProxyComponent = new ComponentName(VCore.get().getHostPkg(), StubManifest.STUB_JOB);
        readJobs();
    }

    private static final Singleton<VJobSchedulerService> gDefault = new Singleton<VJobSchedulerService>() {
        @Override
        protected VJobSchedulerService create() {
            return new VJobSchedulerService();
        }
    };

    public static VJobSchedulerService get() {
        return gDefault.get();
    }


    public static final class JobId implements Parcelable {

        public int vuid;
        public String packageName;
        /**
         * The id given by User.
         */
        public int clientJobId;

        JobId(int vuid, String packageName, int id) {
            this.vuid = vuid;
            this.packageName = packageName;
            this.clientJobId = id;
        }


        JobId(Parcel in) {
            this.vuid = in.readInt();
            this.packageName = in.readString();
            this.clientJobId = in.readInt();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JobId jobId = (JobId) o;

            return vuid == jobId.vuid
                    && clientJobId == jobId.clientJobId
                    && TextUtils.equals(packageName, jobId.packageName);
        }

        @Override
        public int hashCode() {
            int result = vuid;
            result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
            result = 31 * result + clientJobId;
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.vuid);
            dest.writeString(this.packageName);
            dest.writeInt(this.clientJobId);
        }

        public static final Creator<JobId> CREATOR = new Creator<JobId>() {
            @Override
            public JobId createFromParcel(Parcel source) {
                return new JobId(source);
            }

            @Override
            public JobId[] newArray(int size) {
                return new JobId[size];
            }
        };
    }

    public static final class JobConfig implements Parcelable {

        /**
         * The id given by VA.
         */
        public int virtualJobId;
        public String serviceName;
        public PersistableBundle extras;

        JobConfig(int virtualJobId, String serviceName, PersistableBundle extra) {
            this.virtualJobId = virtualJobId;
            this.serviceName = serviceName;
            this.extras = extra;
        }

        JobConfig(Parcel in) {
            this.virtualJobId = in.readInt();
            this.serviceName = in.readString();
            this.extras = in.readParcelable(PersistableBundle.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.virtualJobId);
            dest.writeString(this.serviceName);
            dest.writeParcelable(this.extras, flags);
        }

        public static final Creator<JobConfig> CREATOR = new Creator<JobConfig>() {
            @Override
            public JobConfig createFromParcel(Parcel source) {
                return new JobConfig(source);
            }

            @Override
            public JobConfig[] newArray(int size) {
                return new JobConfig[size];
            }
        };
    }


    @Override
    public int schedule(int uid, JobInfo job) {
        int id = job.getId();
        ComponentName service = job.getService();
        JobId jobId = new JobId(uid, service.getPackageName(), id);
        JobConfig config;
        synchronized (mJobStore) {
            config = mJobStore.get(jobId);
            if(config == null){
                int jid = mNextJobId;
                mNextJobId++;
                config = new JobConfig(jid, service.getClassName(), job.getExtras());
                mJobStore.put(jobId, config);
            }
        }
        config.serviceName = service.getClassName();
        config.extras = job.getExtras();
        saveJobs();
        mirror.vbox.app.job.JobInfo.jobId.set(job, config.virtualJobId);
        mirror.vbox.app.job.JobInfo.service.set(job, mJobProxyComponent);
        return mScheduler.schedule(job);
    }

    private void saveJobs() {
        File jobFile = VEnvironment.getJobConfigFile();
        Parcel p = Parcel.obtain();
        try {
            p.writeInt(JOB_FILE_VERSION);
            p.writeInt(mJobStore.size());
            for (Map.Entry<JobId, JobConfig> entry : mJobStore.entrySet()) {
                entry.getKey().writeToParcel(p, 0);
                entry.getValue().writeToParcel(p, 0);
            }
            FileOutputStream fos = new FileOutputStream(jobFile);
            fos.write(p.marshall());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            p.recycle();
        }
    }

    private void readJobs() {
        File jobFile = VEnvironment.getJobConfigFile();
        if (!jobFile.exists()) {
            return;
        }
        Parcel p = Parcel.obtain();
        try {
            FileInputStream fis = new FileInputStream(jobFile);
            byte[] bytes = new byte[(int) jobFile.length()];
            int len = fis.read(bytes);
            fis.close();
            if (len != bytes.length) {
                throw new IOException("Unable to read job config.");
            }
            p.unmarshall(bytes, 0, bytes.length);
            p.setDataPosition(0);
            int version = p.readInt();
            if (version != JOB_FILE_VERSION) {
                throw new IOException("Bad version of job file: " + version);
            }
            if (!mJobStore.isEmpty()) {
                mJobStore.clear();
            }
            int count = p.readInt();
            int max = 0;
            for (int i = 0; i < count; i++) {
                JobId jobId = new JobId(p);
                JobConfig config = new JobConfig(p);
                mJobStore.put(jobId, config);
                max = Math.max(max, config.virtualJobId);
            }
            mNextJobId = max + 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            p.recycle();
        }

    }

    @Override
    public void cancel(int uid, int jobId) {
        synchronized (mJobStore) {
            boolean changed = false;
            Iterator<Map.Entry<JobId, JobConfig>> iterator = mJobStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<JobId, JobConfig> entry = iterator.next();
                JobId job = entry.getKey();
                JobConfig config = entry.getValue();
                if ((uid == -1 || job.vuid == uid) && job.clientJobId == jobId) {
                    changed = true;
                    mScheduler.cancel(config.virtualJobId);
                    iterator.remove();
                    break;
                }
            }
            if (changed) {
                saveJobs();
            }
        }
    }

    @Override
    public void cancelAll(int uid) {
        synchronized (mJobStore) {
            boolean changed = false;
            Iterator<Map.Entry<JobId, JobConfig>> iterator = mJobStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<JobId, JobConfig> entry = iterator.next();
                JobId job = entry.getKey();
                if (job.vuid == uid) {
                    JobConfig config = entry.getValue();
                    mScheduler.cancel(config.virtualJobId);
                    changed = true;
                    iterator.remove();
                    break;
                }
            }
            if (changed) {
                saveJobs();
            }
        }
    }

    @Override
    public List<JobInfo> getAllPendingJobs(int uid) {
        List<JobInfo> jobs = mScheduler.getAllPendingJobs();
        synchronized (mJobStore) {
            Iterator<JobInfo> iterator = jobs.listIterator();
            while (iterator.hasNext()) {
                JobInfo job = iterator.next();
                if (!StubManifest.STUB_JOB.equals(job.getService().getClassName())) {
                    // Schedule by Host, invisible in VA.
                    iterator.remove();
                    continue;
                }
                Map.Entry<JobId, JobConfig> jobEntry = findJobByVirtualJobId(job.getId());
                if (jobEntry == null) {
                    iterator.remove();
                    continue;
                }
                JobId jobId = jobEntry.getKey();
                JobConfig config = jobEntry.getValue();
                if (jobId.vuid != uid) {
                    iterator.remove();
                    continue;
                }
                mirror.vbox.app.job.JobInfo.jobId.set(job, jobId.clientJobId);
                mirror.vbox.app.job.JobInfo.service.set(job, new ComponentName(jobId.packageName, config.serviceName));
            }
        }
        return jobs;
    }


    public Map.Entry<JobId, JobConfig> findJobByVirtualJobId(int virtualJobId) {
        synchronized (mJobStore) {
            for (Map.Entry<JobId, JobConfig> entry : mJobStore.entrySet()) {
                if (entry.getValue().virtualJobId == virtualJobId) {
                    return entry;
                }
            }
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public JobInfo getPendingJob(int uid, int jobId){
        JobInfo jobInfo = null;
        synchronized (mJobStore) {
            Iterator<Map.Entry<JobId, JobConfig>> iterator = mJobStore.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<JobId, JobConfig> entry = iterator.next();
                JobId job = entry.getKey();
                if (job.vuid == uid && job.clientJobId == jobId) {
                    jobInfo = mScheduler.getPendingJob(job.clientJobId);
                    break;
                }
            }
        }
        return jobInfo;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int enqueue(int uid, JobInfo job, VJobWorkItem workItem){
        if(workItem.get() == null){
            return -1;
        }
        int id = job.getId();
        ComponentName service = job.getService();
        JobId jobId = new JobId(uid, service.getPackageName(), id);
        JobConfig config;
        synchronized (mJobStore) {
            config = mJobStore.get(jobId);
            if(config == null){
                int jid = mNextJobId;
                mNextJobId++;
                config = new JobConfig(jid, service.getClassName(), job.getExtras());
                mJobStore.put(jobId, config);
            }
        }
        config.serviceName = service.getClassName();
        config.extras = job.getExtras();

        saveJobs();
        mirror.vbox.app.job.JobInfo.jobId.set(job, config.virtualJobId);
        mirror.vbox.app.job.JobInfo.service.set(job, mJobProxyComponent);
        return mScheduler.enqueue(job, workItem.get());
    }
}
