package com.fun.vbox.client.hook.proxies.battery_stats;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.health.SystemHealthManager;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.hook.base.BinderInvocationProxy;
import com.fun.vbox.client.hook.base.ReplaceLastUidMethodProxy;

import java.lang.reflect.Method;

import mirror.com.android.internal.app.IBatteryStats;

@TargetApi(Build.VERSION_CODES.N)
public class BatteryStatsHub extends BinderInvocationProxy {

    private static final String SERVICE_NAME = "batterystats";

    public BatteryStatsHub() {
        super(IBatteryStats.Stub.asInterface, SERVICE_NAME);
    }

    @Override
    public void inject() throws Throwable {
        super.inject();
        if (mirror.vbox.os.health.SystemHealthManager.mBatteryStats != null) {
            SystemHealthManager manager = (SystemHealthManager) VCore.get().getContext().getSystemService(Context.SYSTEM_HEALTH_SERVICE);
            mirror.vbox.os.health.SystemHealthManager.mBatteryStats.set(manager, getInvocationStub().getProxyInterface());
        }
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        //android.permission.BATTERY_STATS need system app
        addMethodProxy(new ReplaceLastUidMethodProxy("takeUidSnapshot") {
            @Override
            public Object call(Object who, Method method, Object... args) {
                try {
                    return super.call(who, method, args);
                } catch (Throwable e) {
                    return null;
                }
            }
        });
    }
}
