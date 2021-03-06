package com.zb.vv.client.stub;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.fun.vbox.client.stub.StubManifest;
import com.fun.vbox.helper.compat.BundleCompat;
import com.fun.vbox.server.IRequestPermissionsResult;


@TargetApi(Build.VERSION_CODES.M)
public class RequestPermissionsActivity extends Activity {
    private static final int REQUEST_PERMISSION_CODE = 996;

    public static void request(Context context, boolean is64bit, String[] permissions, IRequestPermissionsResult callback) {
        Intent intent = new Intent();
        if (is64bit) {
            intent.setClassName(StubManifest.PACKAGE_NAME_64BIT, RequestPermissionsActivity.class.getName());
        } else {
            intent.setClassName(StubManifest.PACKAGE_NAME, RequestPermissionsActivity.class.getName());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("permissions", permissions);
        BundleCompat.putBinder(intent, "callback", callback.asBinder());
        context.startActivity(intent);
    }

    private IRequestPermissionsResult mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        final String[] permissions = intent.getStringArrayExtra("permissions");
        IBinder binder = BundleCompat.getBinder(intent, "callback");
        if (binder == null || permissions == null) {
            finish();
            return;
        }
        mCallBack = IRequestPermissionsResult.Stub.asInterface(binder);
        RequestPermissionsActivity.this.requestPermissions(permissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, final String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCallBack != null) {
            try {
                boolean success = mCallBack.onResult(requestCode, permissions, grantResults);
                if (!success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RequestPermissionsActivity.this, "Request permission failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        finish();
    }
}
