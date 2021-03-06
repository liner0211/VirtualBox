package com.zb.vv.client.stub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import com.zb.vv.R;
import com.fun.vbox.client.env.Constants;
import com.fun.vbox.helper.compat.BundleCompat;
import com.fun.vbox.helper.utils.VLog;
import com.fun.vbox.os.VUserHandle;

public class ChooserActivity extends ResolverActivity {
    public static final String EXTRA_DATA = "android.intent.extra.virtual.data";
    public static final String EXTRA_WHO = "android.intent.extra.virtual.who";
    public static final String EXTRA_INTENT = "android.intent.extra.virtual.intent";
    public static final String EXTRA_REQUEST_CODE = "android.intent.extra.virtual.request_code";
    public static final String ACTION;
    public static final String EXTRA_RESULTTO = "_va|ibinder|resultTo";

    static {
        Intent target = new Intent();
        Intent intent = Intent.createChooser(target, "");
        ACTION = intent.getAction();
    }
    public static boolean check(Intent intent) {
        try {
            return TextUtils.equals(ACTION, intent.getAction())
                    ||TextUtils.equals(Intent.ACTION_CHOOSER, intent.getAction());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();
        int userId = extras.getInt(Constants.EXTRA_USER_HANDLE, VUserHandle.getCallingUserId());
        //va api
        mOptions = extras.getParcelable(EXTRA_DATA);
        mResultWho = extras.getString(EXTRA_WHO);
        mRequestCode = extras.getInt(EXTRA_REQUEST_CODE, 0);
        mResultTo = BundleCompat.getBinder(extras, EXTRA_RESULTTO);
        //system api
        Parcelable targetParcelable = intent.getParcelableExtra(Intent.EXTRA_INTENT);
        if (!(targetParcelable instanceof Intent)) {
            VLog.w("ChooseActivity", "Target is not an intent: %s", targetParcelable);
            finish();
            return;
        }
        Intent target = (Intent) targetParcelable;
        CharSequence title = intent.getCharSequenceExtra(Intent.EXTRA_TITLE);
        if (title == null) {
            title = getString(R.string.choose);
        }
        Parcelable[] pa = intent.getParcelableArrayExtra(Intent.EXTRA_INITIAL_INTENTS);
        Intent[] initialIntents = null;
        if (pa != null) {
            initialIntents = new Intent[pa.length];
            for (int i = 0; i < pa.length; i++) {
                if (!(pa[i] instanceof Intent)) {
                    VLog.w("ChooseActivity", "Initial intent #" + i
                            + " not an Intent: %s", pa[i]);
                    finish();
                    return;
                }
                initialIntents[i] = (Intent) pa[i];
            }
        }
        super.onCreate(savedInstanceState, target, title, initialIntents, null, false, userId);
    }
}
