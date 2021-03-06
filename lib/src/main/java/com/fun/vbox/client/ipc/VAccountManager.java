package com.fun.vbox.client.ipc;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.IAccountManagerResponse;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

import com.fun.vbox.client.env.VirtualRuntime;
import com.fun.vbox.client.stub.AmsTask;
import com.fun.vbox.helper.utils.IInterfaceUtils;
import com.fun.vbox.os.VUserHandle;
import com.fun.vbox.server.interfaces.IAccountManager;

import java.util.Map;

import static com.fun.vbox.helper.compat.AccountManagerCompat.KEY_ANDROID_PACKAGE_NAME;

/**
 * @author Lody
 */

public class VAccountManager {

    private static VAccountManager sMgr = new VAccountManager();

    public static VAccountManager get() {
        return sMgr;
    }

    private IAccountManager mService;

    public IAccountManager getService() {
        if (mService == null || !IInterfaceUtils.isAlive(mService)) {
            synchronized (VAccountManager.class) {
                Object remote = getStubInterface();
                mService = LocalProxyUtils.genProxy(IAccountManager.class, remote);
            }
        }
        return mService;
    }

    private Object getStubInterface() {
        return IAccountManager.Stub
                .asInterface(ServiceManagerNative.getService(ServiceManagerNative.ACCOUNT));
    }

    public AuthenticatorDescription[] getAuthenticatorTypes() {
        try {
            return getService().getAuthenticatorTypes(VUserHandle.myUserId());
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, null);
        }
    }

    public void removeAccount(IAccountManagerResponse response, Account account, boolean expectActivityLaunch) {
        try {
            getService().removeAccount(VUserHandle.myUserId(), response, account, expectActivityLaunch);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAuthToken(IAccountManagerResponse response, Account account, String authTokenType, boolean notifyOnAuthFailure, boolean expectActivityLaunch, Bundle loginOptions) {
        try {
            getService().getAuthToken(VUserHandle.myUserId(), response, account, authTokenType, notifyOnAuthFailure, expectActivityLaunch, loginOptions);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean addAccountExplicitly(Account account, String password, Bundle extras) {
        try {
            return getService().addAccountExplicitly(VUserHandle.myUserId(), account, password, extras);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, false);
        }
    }

    public Account[] getAccounts(int userId, String type) {
        try {
            return getService().getAccounts(userId, type);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, null);
        }
    }

    public Account[] getAccounts(String type) {
        try {
            return getService().getAccounts(VUserHandle.myUserId(), type);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, null);
        }
    }

    public String peekAuthToken(Account account, String authTokenType) {
        try {
            return getService().peekAuthToken(VUserHandle.myUserId(), account, authTokenType);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, "");
        }
    }

    public String getPreviousName(Account account) {
        try {
            return getService().getPreviousName(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, "");
        }
    }

    public void hasFeatures(IAccountManagerResponse response, Account account, String[] features) {
        try {
            getService().hasFeatures(VUserHandle.myUserId(), response, account, features);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean accountAuthenticated(Account account) {
        try {
            return getService().accountAuthenticated(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, false);
        }
    }

    public void clearPassword(Account account) {
        try {
            getService().clearPassword(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void renameAccount(IAccountManagerResponse response, Account accountToRename, String newName) {
        try {
            getService().renameAccount(VUserHandle.myUserId(), response, accountToRename, newName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(Account account, String password) {
        try {
            getService().setPassword(VUserHandle.myUserId(), account, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(int userId, IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle optionsIn) {
        try {
            getService().addAccount(userId, response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, optionsIn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(IAccountManagerResponse response, String accountType, String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch, Bundle optionsIn) {
        try {
            getService().addAccount(VUserHandle.myUserId(), response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, optionsIn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateCredentials(IAccountManagerResponse response, Account account, String authTokenType, boolean expectActivityLaunch, Bundle loginOptions) {
        try {
            getService().updateCredentials(VUserHandle.myUserId(), response, account, authTokenType, expectActivityLaunch, loginOptions);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean removeAccountExplicitly(Account account) {
        try {
            return getService().removeAccountExplicitly(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, false);
        }
    }

    public void setUserData(Account account, String key, String value) {
        try {
            getService().setUserData(VUserHandle.myUserId(), account, key, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void editProperties(IAccountManagerResponse response, String accountType, boolean expectActivityLaunch) {
        try {
            getService().editProperties(VUserHandle.myUserId(), response, accountType, expectActivityLaunch);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAuthTokenLabel(IAccountManagerResponse response, String accountType, String authTokenType) {
        try {
            getService().getAuthTokenLabel(VUserHandle.myUserId(), response, accountType, authTokenType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void confirmCredentials(IAccountManagerResponse response, Account account, Bundle options, boolean expectActivityLaunch) {
        try {
            getService().confirmCredentials(VUserHandle.myUserId(), response, account, options, expectActivityLaunch);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void invalidateAuthToken(String accountType, String authToken) {
        try {
            getService().invalidateAuthToken(VUserHandle.myUserId(), accountType, authToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAccountsByFeatures(IAccountManagerResponse response, String type, String[] features) {
        try {
            getService().getAccountsByFeatures(VUserHandle.myUserId(), response, type, features);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setAuthToken(Account account, String authTokenType, String authToken) {
        try {
            getService().setAuthToken(VUserHandle.myUserId(), account, authTokenType, authToken);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Object getPassword(Account account) {
        try {
            return getService().getPassword(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, null);
        }
    }

    public String getUserData(Account account, String key) {
        try {
            return getService().getUserData(VUserHandle.myUserId(), account, key);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, "");
        }
    }

    /**
     * Asks the user to add an account of a specified type.  The authenticator
     * for this account type processes this request with the appropriate user
     * interface.  If the user does elect to create a new account, the account
     * name is returned.
     * <p>
     * <p>This method may be called from any thread, but the returned
     * {@link AccountManagerFuture} must not be used on the main thread.
     * <p>
     */
    public AccountManagerFuture<Bundle> addAccount(final int userId, final String accountType,
                                                   final String authTokenType, final String[] requiredFeatures,
                                                   final Bundle addAccountOptions,
                                                   final Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        if (accountType == null) throw new IllegalArgumentException("accountType is null");
        final Bundle optionsIn = new Bundle();
        if (addAccountOptions != null) {
            optionsIn.putAll(addAccountOptions);
        }
        optionsIn.putString(KEY_ANDROID_PACKAGE_NAME, "android");
        return new AmsTask(activity, handler, callback) {
            @Override
            public void doWork() throws RemoteException {
                addAccount(userId, mResponse, accountType, authTokenType,
                        requiredFeatures, activity != null, optionsIn);
            }
        }.start();
    }


    public boolean setAccountVisibility(Account a, String packageName, int newVisibility) {
        try {
            return getService().setAccountVisibility(VUserHandle.myUserId(), a, packageName, newVisibility);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, false);
        }
    }

    public int getAccountVisibility( Account a, String packageName) {
        try {
            return getService().getAccountVisibility(VUserHandle.myUserId(), a, packageName);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, 0);
        }
    }

    public void startAddAccountSession(IAccountManagerResponse response, String accountType,
                                       String authTokenType, String[] requiredFeatures, boolean expectActivityLaunch,
                                       Bundle options) {
        try {
            getService().startAddAccountSession(response, accountType, authTokenType, requiredFeatures, expectActivityLaunch, options);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void startUpdateCredentialsSession(IAccountManagerResponse response, Account account,
                                              String authTokenType, boolean expectActivityLaunch, Bundle options) {
        try {
            getService().startUpdateCredentialsSession(response, account, authTokenType, expectActivityLaunch, options);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void registerAccountListener(String[] accountTypes, String opPackageName) {
        try {
            getService().registerAccountListener(accountTypes, opPackageName);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void unregisterAccountListener(String[] accountTypes, String opPackageName) {
        try {
            getService().unregisterAccountListener(accountTypes, opPackageName);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public Map getPackagesAndVisibilityForAccount(Account account) {
        try {
            return getService().getPackagesAndVisibilityForAccount(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, null);
        }
    }

    public Map getAccountsAndVisibilityForPackage(String packageName, String accountType) {
        try {
            return getService().getAccountsAndVisibilityForPackage(VUserHandle.myUserId(), packageName, accountType);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, null);
        }
    }

    public void finishSessionAsUser(IAccountManagerResponse response, Bundle sessionBundle,
                                    boolean expectActivityLaunch, Bundle appInfo, int userId) {
        try {
            getService().finishSessionAsUser(response, sessionBundle, expectActivityLaunch, appInfo, userId);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void isCredentialsUpdateSuggested(IAccountManagerResponse response, Account account,
                                             String statusToken) {
        try {
            getService().isCredentialsUpdateSuggested(response, account, statusToken);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public boolean addAccountExplicitlyWithVisibility(Account account, String password, Bundle extras,
                                                      Map visibility) {
        try {
            return getService().addAccountExplicitlyWithVisibility(VUserHandle.myUserId(), account, password, extras, visibility);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e, false);
        }
    }
}
