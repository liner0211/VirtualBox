package mirror.vbox.content.pm;

import android.content.pm.ApplicationInfo;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefMethod;

public class ApplicationInfoP {
    public static Class<?> TYPE = RefClass.load(ApplicationInfoP.class, ApplicationInfo.class);
    @MethodParams(int.class)
    public static RefMethod<Void> setHiddenApiEnforcementPolicy;
}
