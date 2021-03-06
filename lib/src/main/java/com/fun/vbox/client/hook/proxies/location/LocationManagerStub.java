package com.fun.vbox.client.hook.proxies.location;

import android.content.Context;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;

import com.fun.vbox.client.hook.annotations.Inject;
import com.fun.vbox.client.hook.base.BinderInvocationStub;
import com.fun.vbox.client.hook.base.MethodInvocationProxy;
import com.fun.vbox.client.hook.base.ReplaceLastPkgMethodProxy;
import com.fun.vbox.client.hook.base.ReplaceLastUserIdMethodProxy;
import com.fun.vbox.client.hook.base.StaticMethodProxy;
import com.fun.vbox.helper.compat.BuildCompat;
import com.fun.vbox.helper.utils.Reflect;
import com.fun.vbox.helper.utils.ReflectException;

import java.lang.reflect.Method;

import mirror.vbox.location.ILocationManager;
import mirror.vbox.os.ServiceManager;

/**
 *
 * @see android.location.LocationManager
 */
@Inject(MethodProxies.class)
public class LocationManagerStub extends MethodInvocationProxy<BinderInvocationStub> {
    public LocationManagerStub() {
        super(new BinderInvocationStub(getInterface()));
    }

    private static IInterface getInterface() {
        IBinder base = ServiceManager.getService.call(Context.LOCATION_SERVICE);
        if (base instanceof Binder) {
            try {
                return Reflect.on(base).get("mILocationManager");
            } catch (ReflectException e) {
                e.printStackTrace();
            }
        }
        return ILocationManager.Stub.asInterface.call(base);
    }

    @Override
    public void inject() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Object base = mirror.vbox.location.LocationManager.mService.get(locationManager);
        if (base instanceof Binder) {
            Reflect.on(base).set("mILocationManager", getInvocationStub().getProxyInterface());
        }
        mirror.vbox.location.LocationManager.mService.set(locationManager, getInvocationStub().getProxyInterface());
        getInvocationStub().replaceService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean isEnvBad() {
        return false;
    }


    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addMethodProxy(new ReplaceLastPkgMethodProxy("addTestProvider"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("removeTestProvider"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("setTestProviderLocation"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("clearTestProviderLocation"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("setTestProviderEnabled"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("clearTestProviderEnabled"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("setTestProviderStatus"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("clearTestProviderStatus"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addGpsMeasurementListener", true));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addGpsNavigationMessageListener", true));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeGpsMeasurementListener", 0));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeGpsNavigationMessageListener", 0));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("requestGeofence", 0));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeGeofence", 0));
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            addMethodProxy(new MethodProxies.GetLastKnownLocation());
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addProximityAlert", 0));
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            addMethodProxy(new MethodProxies.RequestLocationUpdatesPI());
            addMethodProxy(new MethodProxies.RemoveUpdatesPI());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            addMethodProxy(new MethodProxies.RequestLocationUpdates());
            addMethodProxy(new MethodProxies.RemoveUpdates());
        }

        addMethodProxy(new MethodProxies.IsProviderEnabled());
        addMethodProxy(new MethodProxies.GetBestProvider());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addMethodProxy(new MethodProxies.GetLastLocation());
            addMethodProxy(new MethodProxies.AddGpsStatusListener());
            addMethodProxy(new MethodProxies.RemoveGpsStatusListener());
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addNmeaListener", 0));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeNmeaListener", 0));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addMethodProxy(new MethodProxies.RegisterGnssStatusCallback());
            addMethodProxy(new MethodProxies.UnregisterGnssStatusCallback());
        }
        addMethodProxy(new ReplaceLastUserIdMethodProxy("isProviderEnabledForUser"));
        addMethodProxy(new ReplaceLastUserIdMethodProxy("isLocationEnabledForUser"));
        if (BuildCompat.isQ()) {
            addMethodProxy(new StaticMethodProxy("setLocationControllerExtraPackageEnabled") {
                public Object call(Object obj, Method method, Object... objArr) {
                    return null;
                }
            });
            addMethodProxy(new StaticMethodProxy("setExtraLocationControllerPackageEnabled") {
                public Object call(Object obj, Method method, Object... objArr) {
                    return null;
                }
            });
        }
    }

    private static class FakeReplaceLastPkgMethodProxy extends ReplaceLastPkgMethodProxy {
        private Object mDefValue;

        private FakeReplaceLastPkgMethodProxy(String name, Object def) {
            super(name);
            mDefValue = def;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                return mDefValue;
            }
            return super.call(who, method, args);
        }
    }
}
