package com.fun.vbox.client.ipc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.helper.compat.BundleCompat;
import com.fun.vbox.helper.compat.ContentProviderCompat;

import java.io.Serializable;

/**
 * @author Lody
 */
public class ProviderCall {

    public static Bundle callSafely(String authority, String methodName, String arg, Bundle bundle) {
        try {
            return call(authority, VCore.get().getContext(), methodName, arg, bundle, 5);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bundle call(String authority, Context context, String method, String arg, Bundle bundle, int retryCount) throws IllegalAccessException {
        Uri uri = Uri.parse("content://" + authority);
        return ContentProviderCompat.call(context, uri, method, arg, bundle, retryCount);
    }

    public static Bundle callWithException(String authority, String methodName, String arg, Bundle bundle) throws IllegalAccessException {
        return call(authority, VCore.get().getContext(), methodName, arg, bundle, 5);
    }

    public static final class Builder {

        private Context context;

        private Bundle bundle = new Bundle();

        private String method;
        private String auth;
        private String arg;
        private int retryCount = 5;

        public Builder(Context context, String auth) {
            this.context = context;
            this.auth = auth;
        }

        public Builder methodName(String name) {
            this.method = name;
            return this;
        }

        public Builder arg(String arg) {
            this.arg = arg;
            return this;
        }

        public Builder addArg(String key, Object value) {
            if (value != null) {
                if (value instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    bundle.putInt(key, (Integer) value);
                } else if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Serializable) {
                    bundle.putSerializable(key, (Serializable) value);
                } else if (value instanceof Bundle) {
                    bundle.putBundle(key, (Bundle) value);
                } else if (value instanceof Parcelable) {
                    bundle.putParcelable(key, (Parcelable) value);
                } else if (value instanceof int[]) {
                    bundle.putIntArray(key, (int[]) value);
                } else if (value instanceof IBinder) {
                    BundleCompat.putBinder(bundle, key, (IBinder) value);
                } else {
                    throw new IllegalArgumentException("Unknown type " + value.getClass() + " in Bundle.");
                }
            }
            return this;
        }

        public Builder retry(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Bundle call() throws IllegalAccessException {
            return ProviderCall.call(auth, context, method, arg, bundle, retryCount);
        }

        public Bundle callSafely() {
            try {
                return call();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
