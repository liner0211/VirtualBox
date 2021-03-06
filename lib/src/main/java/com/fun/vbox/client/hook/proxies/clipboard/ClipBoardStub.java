package com.fun.vbox.client.hook.proxies.clipboard;

import android.content.Context;
import android.os.Build;
import android.os.IInterface;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.hook.base.BinderInvocationProxy;
import com.fun.vbox.client.hook.base.ReplaceLastPkgMethodProxy;

import mirror.vbox.content.ClipboardManager;
import mirror.vbox.content.ClipboardManagerOreo;

/**
 *
 * @see ClipboardManager
 */
public class ClipBoardStub extends BinderInvocationProxy {

    public ClipBoardStub() {
        super(getInterface(), Context.CLIPBOARD_SERVICE);
    }

    private static IInterface getInterface() {
        // android < 26
        if (ClipboardManager.getService != null) {
            return ClipboardManager.getService.call();
        } else if (ClipboardManagerOreo.mService != null) {
            android.content.ClipboardManager cm = (android.content.ClipboardManager)
                    VCore.get().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            return ClipboardManagerOreo.mService.get(cm);
        } else if (ClipboardManagerOreo.sService != null) {
            //samsung
            return ClipboardManagerOreo.sService.get();
        } else {
            return null;
        }
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new ReplaceLastPkgMethodProxy("getPrimaryClip"));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addMethodProxy(new ReplaceLastPkgMethodProxy("setPrimaryClip"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("getPrimaryClipDescription"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("hasPrimaryClip"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("addPrimaryClipChangedListener"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("removePrimaryClipChangedListener"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("hasClipboardText"));
        }
    }

    @Override
    public void inject() throws Throwable {
        super.inject();
        if (ClipboardManagerOreo.mService != null) {
            android.content.ClipboardManager cm = (android.content.ClipboardManager)
                    VCore.get().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipboardManagerOreo.mService.set(cm, getInvocationStub().getProxyInterface());
        } else if (ClipboardManagerOreo.sService != null) {
            //samsung 8.0
            ClipboardManagerOreo.sService.set(getInvocationStub().getProxyInterface());
        }
    }
}
