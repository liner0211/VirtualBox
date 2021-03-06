package com.fun.vbox.client.hook.proxies.location;

import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;

import com.fun.vbox.client.hook.base.MethodProxy;
import com.fun.vbox.client.hook.base.ReplaceLastPkgMethodProxy;
import com.fun.vbox.client.hook.annotations.SkipInject;
import com.fun.vbox.client.ipc.VLocationManager;
import com.fun.vbox.helper.utils.Reflect;
import com.fun.vbox.remote.vloc.VLocation;

import java.lang.reflect.Method;
import java.util.Arrays;

import mirror.vbox.location.LocationRequestL;

/**
 * @author Lody
 */
@SuppressWarnings("ALL")
class MethodProxies {

    private static void fixLocationRequest(LocationRequest request) {
        if (request != null) {
            if (LocationRequestL.mHideFromAppOps != null) {
                LocationRequestL.mHideFromAppOps.set(request, false);
            }
            if (LocationRequestL.mWorkSource != null) {
                LocationRequestL.mWorkSource.set(request, null);
            }
        }
    }

    @SkipInject
    static class AddGpsStatusListener extends ReplaceLastPkgMethodProxy {
        public AddGpsStatusListener() {
            super("addGpsStatusListener");
        }

        public AddGpsStatusListener(String name) {
            super(name);
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                VLocationManager.get().addGpsStatusListener(args);
                return true;
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class RequestLocationUpdates extends ReplaceLastPkgMethodProxy {

        public RequestLocationUpdates() {
            super("requestLocationUpdates");
        }

        public RequestLocationUpdates(String name) {
            super(name);
        }

        @Override
        public Object call(final Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                VLocationManager.get().requestLocationUpdates(args);
                return 0;
            } else {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    LocationRequest request = (LocationRequest) args[0];
                    fixLocationRequest(request);
                }
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class RemoveUpdates extends ReplaceLastPkgMethodProxy {

        public RemoveUpdates() {
            super("removeUpdates");
        }

        public RemoveUpdates(String name) {
            super(name);
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                VLocationManager.get().removeUpdates(args);
                return 0;
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class GetLastLocation extends ReplaceLastPkgMethodProxy {

        public GetLastLocation() {
            super("getLastLocation");
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (!(args[0] instanceof String)) {
                LocationRequest request = (LocationRequest) args[0];
                fixLocationRequest(request);
            }
            if (isFakeLocationEnable()) {
                VLocation loc= VLocationManager.get().getLocation(getAppPkg(), getAppUserId());
                if(loc!=null){
                    return loc.toSysLocation();
                }
                return null;
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class GetLastKnownLocation extends GetLastLocation {
        @Override
        public String getMethodName() {
            return "getLastKnownLocation";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                VLocation loc = VLocationManager.get().getLocation(getAppPkg(), getAppUserId());
                if (loc != null) {
                    return loc.toSysLocation();
                }
                return null;
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class IsProviderEnabled extends MethodProxy {
        @Override
        public String getMethodName() {
            return "isProviderEnabled";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                if (args[0] instanceof String) {
                    return VLocationManager.get().isProviderEnabled((String) args[0]);
                }
            }
            return super.call(who, method, args);
        }
    }

    private static class getAllProviders extends MethodProxy {

        @Override
        public String getMethodName() {
            return "getAllProviders";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                return Arrays.asList(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class GetBestProvider extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getBestProvider";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                return LocationManager.GPS_PROVIDER;
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class RemoveGpsStatusListener extends ReplaceLastPkgMethodProxy {
        public RemoveGpsStatusListener() {
            super("removeGpsStatusListener");
        }

        public RemoveGpsStatusListener(String name) {
            super(name);
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                VLocationManager.get().removeGpsStatusListener(args);
                return 0;
            }
            return super.call(who, method, args);
        }
    }

    @SkipInject
    static class RequestLocationUpdatesPI extends RequestLocationUpdates {
        public RequestLocationUpdatesPI() {
            super("requestLocationUpdatesPI");
        }
    }

    @SkipInject
    static class RemoveUpdatesPI extends RemoveUpdates {
        public RemoveUpdatesPI() {
            super("removeUpdatesPI");
        }
    }

    @SkipInject
    static class UnregisterGnssStatusCallback extends RemoveGpsStatusListener {
        public UnregisterGnssStatusCallback() {
            super("unregisterGnssStatusCallback");
        }
    }

    @SkipInject
    static class RegisterGnssStatusCallback extends AddGpsStatusListener {
        public RegisterGnssStatusCallback() {
            super("registerGnssStatusCallback");
        }
    }


    static class sendExtraCommand extends MethodProxy {

        @Override
        public String getMethodName() {
            return "sendExtraCommand";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                return true;
            }
            return super.call(who, method, args);
        }
    }

    static class getProviderProperties extends MethodProxy {

        @Override
        public String getMethodName() {
            return "getProviderProperties";
        }

        @Override
        public Object afterCall(Object who, Method method, Object[] args, Object result) throws Throwable {
            if (isFakeLocationEnable()) {
                try {
                    Reflect.on(result).set("mRequiresNetwork", false);
                    Reflect.on(result).set("mRequiresCell", false);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return result;
            }
            return super.afterCall(who, method, args, result);
        }
    }

    static class locationCallbackFinished extends MethodProxy {

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            return super.call(who, method, args);
        }

        @Override
        public String getMethodName() {
            return "locationCallbackFinished";
        }
    }
}
