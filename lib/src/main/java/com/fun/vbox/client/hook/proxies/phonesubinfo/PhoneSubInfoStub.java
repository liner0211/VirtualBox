package com.fun.vbox.client.hook.proxies.phonesubinfo;

import com.fun.vbox.client.hook.base.BinderInvocationProxy;
import com.fun.vbox.client.hook.annotations.Inject;
import com.fun.vbox.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.fun.vbox.client.hook.base.ReplaceLastPkgMethodProxy;

import mirror.com.android.internal.telephony.IPhoneSubInfo;

/**
 *
 * @see android.telephony.TelephonyManager
 */
@Inject(MethodProxies.class)
public class PhoneSubInfoStub extends BinderInvocationProxy {
	public PhoneSubInfoStub() {
		super(IPhoneSubInfo.Stub.asInterface, "iphonesubinfo");
	}

	@Override
	protected void onBindMethods() {
		super.onBindMethods();
        addMethodProxy(new ReplaceLastPkgMethodProxy("getNaiForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getDeviceSvn"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getDeviceSvnUsingSubId"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getSubscriberId"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getSubscriberIdForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getGroupIdLevel1"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getGroupIdLevel1ForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getLine1AlphaTag"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getLine1AlphaTagForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getMsisdn"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getMsisdnForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getVoiceMailNumber"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getVoiceMailNumberForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getVoiceMailAlphaTag"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getVoiceMailAlphaTagForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getLine1Number"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getLine1NumberForSubscriber"));
    }

}
