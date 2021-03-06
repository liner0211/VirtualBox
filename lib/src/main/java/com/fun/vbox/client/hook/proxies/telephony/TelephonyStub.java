package com.fun.vbox.client.hook.proxies.telephony;

import android.content.Context;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.hook.base.BinderInvocationProxy;
import com.fun.vbox.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.fun.vbox.client.hook.base.ReplaceLastPkgMethodProxy;
import com.fun.vbox.client.hook.base.ResultStaticMethodProxy;
import com.fun.vbox.helper.compat.BuildCompat;

import mirror.com.android.internal.telephony.ITelephony;

/**
 *
 * @see android.telephony.TelephonyManager
 */
public class TelephonyStub extends BinderInvocationProxy {

	public TelephonyStub() {
		super(ITelephony.Stub.asInterface, Context.TELEPHONY_SERVICE);
	}

	@Override
	protected void onBindMethods() {
		super.onBindMethods();
        //phone number
        addMethodProxy(new ReplaceLastPkgMethodProxy("getLine1NumberForDisplay"));
        //fake location
        addMethodProxy(new MethodProxies.GetCellLocation());
        addMethodProxy(new MethodProxies.GetAllCellInfoUsingSubId());
        addMethodProxy(new MethodProxies.GetAllCellInfo());
        addMethodProxy(new MethodProxies.GetNeighboringCellInfo());

        addMethodProxy(new MethodProxies.GetDeviceId());
        addMethodProxy(new MethodProxies.GetImeiForSlot());
        addMethodProxy(new MethodProxies.GetMeidForSlot());
        addMethodProxy(new ReplaceCallingPkgMethodProxy("call"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("isSimPinEnabled"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getCdmaEriIconIndex"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getCdmaEriIconIndexForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getCdmaEriIconMode"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getCdmaEriIconModeForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getCdmaEriText"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getCdmaEriTextForSubscriber"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getNetworkTypeForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getDataNetworkType"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getDataNetworkTypeForSubscriber"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getVoiceNetworkTypeForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getLteOnCdmaMode"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getLteOnCdmaModeForSubscriber"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getCalculatedPreferredNetworkType"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getPcscfAddress"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getLine1AlphaTagForDisplay"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getMergedSubscriberIds"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("getRadioAccessFamily"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("isVideoCallingEnabled"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getDeviceSoftwareVersionForSlot"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getServiceStateForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getVisualVoicemailPackageName"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("enableVisualVoicemailSmsFilter"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("disableVisualVoicemailSmsFilter"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getVisualVoicemailSmsFilterSettings"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("sendVisualVoicemailSmsForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getVoiceActivationState"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getDataActivationState"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getVoiceMailAlphaTagForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("sendDialerSpecialCode"));
        if (BuildCompat.isOreo()) {
            addMethodProxy(new ReplaceCallingPkgMethodProxy("setVoicemailVibrationEnabled"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("setVoicemailRingtoneUri"));
        }
        addMethodProxy(new ReplaceCallingPkgMethodProxy("isOffhook"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("isOffhookForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("isRinging"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("isRingingForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("isIdle"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("isIdleForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("isRadioOn"));
        addMethodProxy(new ReplaceLastPkgMethodProxy("isRadioOnForSubscriber"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getClientRequestStats"));
        //systemApi
        if (!VCore.get().isSystemApp()) {
            addMethodProxy(new ResultStaticMethodProxy("getVisualVoicemailSettings", null));
            addMethodProxy(new ResultStaticMethodProxy("setDataEnabled", 0));
            addMethodProxy(new ResultStaticMethodProxy("getDataEnabled", false));
        }
    }

}
