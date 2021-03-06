package com.fun.vbox.client.hook.proxies.window.session;

import android.os.IInterface;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.hook.base.MethodInvocationProxy;
import com.fun.vbox.client.hook.base.MethodInvocationStub;

import java.lang.reflect.Method;

import mirror.vbox.view.WindowManagerGlobal;

/**
 * @author Lody
 */
public class WindowSessionPatch extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {
    private static final int ADD_PERMISSION_DENIED = WindowManagerGlobal.ADD_PERMISSION_DENIED != null
            ? WindowManagerGlobal.ADD_PERMISSION_DENIED.get() : -8;

	public WindowSessionPatch(IInterface session) {
		super(new MethodInvocationStub<>(session));
	}

	@Override
    public void onBindMethods() {
        addMethodProxy(new BaseMethodProxy("add"));
        addMethodProxy(new BaseMethodProxy("addToDisplay") {
            @Override
            public Object call(Object who, Method method, Object... args) throws Throwable {
                if (isDrawOverlays() && VCore.getConfig().isDisableDrawOverlays(getAppPkg())) {
                    return ADD_PERMISSION_DENIED;
                }
                return super.call(who, method, args);
            }
        });
        addMethodProxy(new BaseMethodProxy("addToDisplayWithoutInputChannel"));
        addMethodProxy(new BaseMethodProxy("addWithoutInputChannel"));
		addMethodProxy(new BaseMethodProxy("relayout"));
        addMethodProxy(new BaseMethodProxy("addToDisplayAsUser"));
	}


	@Override
	public void inject() throws Throwable {
		// <EMPTY>
	}

	@Override
	public boolean isEnvBad() {
		return getInvocationStub().getProxyInterface() != null;
	}
}
