package mirror.vbox.bluetooth;

import android.os.IBinder;
import android.os.IInterface;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

public class IBluetoothManager {
    public static Class<?> TYPE = RefClass.load(IBluetoothManager.class, "android.bluetooth.IBluetoothManager");
    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.bluetooth.IBluetoothManager$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}