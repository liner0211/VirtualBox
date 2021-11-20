package com.fun.vbox.client.hook.proxies.persistent_data_block;

import com.fun.vbox.client.hook.base.BinderInvocationProxy;
import com.fun.vbox.client.hook.base.ResultStaticMethodProxy;

import mirror.vbox.service.persistentdata.IPersistentDataBlockService;

/**
 * @author Lody
 */
public class PersistentDataBlockServiceStub extends BinderInvocationProxy {

	public PersistentDataBlockServiceStub() {
		super(IPersistentDataBlockService.Stub.TYPE, "persistent_data_block");
	}

	@Override
	protected void onBindMethods() {
		super.onBindMethods();
		addMethodProxy(new ResultStaticMethodProxy("write", -1));
		addMethodProxy(new ResultStaticMethodProxy("read", new byte[0]));
		addMethodProxy(new ResultStaticMethodProxy("wipe", null));
		addMethodProxy(new ResultStaticMethodProxy("getDataBlockSize", 0));
		addMethodProxy(new ResultStaticMethodProxy("getMaximumDataBlockSize", 0));
		addMethodProxy(new ResultStaticMethodProxy("setOemUnlockEnabled", 0));
		addMethodProxy(new ResultStaticMethodProxy("getOemUnlockEnabled", false));
	}
}