package com.fun.vbox.client.hook.proxies.pm;

import android.content.Context;
import android.os.IInterface;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.hook.annotations.Inject;
import com.fun.vbox.client.hook.base.BinderInvocationStub;
import com.fun.vbox.client.hook.base.MethodInvocationProxy;
import com.fun.vbox.client.hook.base.MethodInvocationStub;
import com.fun.vbox.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.fun.vbox.client.hook.base.ResultStaticMethodProxy;
import com.fun.vbox.helper.compat.BuildCompat;
import com.fun.vbox.helper.utils.Reflect;

import mirror.vbox.app.ActivityThread;

/**
 * @author Lody
 */
@Inject(MethodProxies.class)
public final class PackageManagerStub extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {

    public PackageManagerStub() {
        super(new MethodInvocationStub<>(ActivityThread.sPackageManager.get()));
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new ResultStaticMethodProxy("addPermissionAsync", true));
        addMethodProxy(new ResultStaticMethodProxy("addPermission", true));
        addMethodProxy(new ResultStaticMethodProxy("performDexOpt", true));
        addMethodProxy(new ResultStaticMethodProxy("performDexOptIfNeeded", false));
        addMethodProxy(new ResultStaticMethodProxy("performDexOptSecondary", true));
        addMethodProxy(new ResultStaticMethodProxy("addOnPermissionsChangeListener", 0));
        addMethodProxy(new ResultStaticMethodProxy("removeOnPermissionsChangeListener", 0));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("shouldShowRequestPermissionRationale"));
        if (BuildCompat.isOreo()) {
            addMethodProxy(new ResultStaticMethodProxy("notifyDexLoad", 0));
            addMethodProxy(new ResultStaticMethodProxy("notifyPackageUse", 0));
            addMethodProxy(new ResultStaticMethodProxy("setInstantAppCookie", false));
            addMethodProxy(new ResultStaticMethodProxy("isInstantApp", false));
        }

    }

    @Override
    public void inject() throws Throwable {
        final IInterface hookedPM = getInvocationStub().getProxyInterface();
        ActivityThread.sPackageManager.set(hookedPM);
        BinderInvocationStub pmHookBinder = new BinderInvocationStub(getInvocationStub().getBaseInterface());
        pmHookBinder.copyMethodProxies(getInvocationStub());
        pmHookBinder.replaceService("package");
        try {
            Context systemContext = Reflect.on(VCore.mainThread()).call("getSystemContext").get();
            Object systemContextPm = Reflect.on(systemContext).field("mPackageManager").get();
            if (systemContextPm != null) {
                Reflect.on(systemContext).field("mPackageManager").set("mPM", hookedPM);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isEnvBad() {
        return getInvocationStub().getProxyInterface() != ActivityThread.sPackageManager.get();
    }
}
