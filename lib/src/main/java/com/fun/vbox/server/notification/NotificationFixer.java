package com.fun.vbox.server.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.helper.compat.BuildCompat;
import com.fun.vbox.helper.utils.BitmapUtils;
import com.fun.vbox.helper.utils.Reflect;

import java.util.ArrayList;
import java.util.List;

import mirror.com.android.internal.R_Hide;

/* package */ class NotificationFixer {

    private static final String TAG = NotificationCompat.TAG;
    private NotificationCompat mNotificationCompat;

    NotificationFixer(NotificationCompat notificationCompat) {
        this.mNotificationCompat = notificationCompat;
    }

    private static void fixNotificationIcon(Context context, Notification notification, Notification.Builder builder) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //noinspection deprecation
            builder.setSmallIcon(notification.icon);
            //noinspection deprecation
            builder.setLargeIcon(notification.largeIcon);
        } else {
            Icon icon = notification.getSmallIcon();
            if (icon != null) {
                Bitmap bitmap = BitmapUtils.drawableToBitmap(icon.loadDrawable(context));
                if (bitmap != null) {
                    Icon newIcon = Icon.createWithBitmap(bitmap);
                    builder.setSmallIcon(newIcon);
                }
            }
            Icon largeIcon = notification.getLargeIcon();
            if (largeIcon != null) {
                Bitmap bitmap = BitmapUtils.drawableToBitmap(largeIcon.loadDrawable(context));
                if (bitmap != null) {
                    Icon newIcon = Icon.createWithBitmap(bitmap);
                    builder.setLargeIcon(newIcon);
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    void fixIcon(Icon icon, Context appContext, boolean installed) {
        if (icon == null) {
            return;
        }
        int type = mirror.vbox.graphics.drawable.Icon.mType.get(icon);
        if (type == mirror.vbox.graphics.drawable.Icon.TYPE_RESOURCE) {
            if (installed) {
                mirror.vbox.graphics.drawable.Icon.mObj1.set(icon, appContext.getResources());
                mirror.vbox.graphics.drawable.Icon.mString1.set(icon, appContext.getPackageName());
            } else {
                Drawable drawable = icon.loadDrawable(appContext);
                Bitmap bitmap = BitmapUtils.drawableToBitmap(drawable);
                mirror.vbox.graphics.drawable.Icon.mObj1.set(icon, bitmap);
                mirror.vbox.graphics.drawable.Icon.mString1.set(icon, null);
                mirror.vbox.graphics.drawable.Icon.mType.set(icon, mirror.vbox.graphics.drawable.Icon.TYPE_BITMAP);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void fixNotificationRemoteViews(Context pluginContext, Notification notification) {
        Notification.Builder rebuild = null;
        try {
            rebuild = Reflect.on(Notification.Builder.class).create(pluginContext, notification).get();
        } catch (Exception e) {
            // ignore
        }
        if (rebuild != null) {
            Notification renotification = rebuild.build();
            if (notification.tickerView == null) {
                notification.tickerView = renotification.tickerView;
            }
            if (notification.contentView == null) {
                notification.contentView = renotification.contentView;
            }
            if (notification.bigContentView == null) {
                notification.bigContentView = renotification.bigContentView;
            }
            if (notification.headsUpContentView == null) {
                notification.headsUpContentView = renotification.headsUpContentView;
            }
        }
    }

    boolean fixRemoteViewActions(Context appContext, boolean installed, final RemoteViews remoteViews) {
        boolean hasIcon = false;
        if (remoteViews != null) {
            int systemIconViewId = R_Hide.id.icon.get();
            List<BitmapReflectionAction> mNew = new ArrayList<>();
            ArrayList<Object> mActions = Reflect.on(remoteViews).get("mActions");
            if (mActions != null) {
                int count = mActions.size();
                for (int i = count - 1; i >= 0; i--) {
                    Object action = mActions.get(i);
                    if (action == null) {
                        continue;
                    }
                    //TextViewDrawableAction
                    //setImageURI
                    //setLabelFor
                    if (action.getClass().getSimpleName().endsWith("TextViewDrawableAction")) {
                        mActions.remove(action);
                        continue;
                    }
                    if (ReflectionActionCompat.isInstance(action)) {
                        int viewId = Reflect.on(action).get("viewId");

                        String methodName = Reflect.on(action).get("methodName");
                        int type = Reflect.on(action).get("type");
                        Object value = Reflect.on(action).get("value");
                        if (!hasIcon) {
                            hasIcon = viewId == systemIconViewId;
                            if (hasIcon) {
                                if (type == ReflectionActionCompat.INT && (int) value == 0) {
                                    hasIcon = false;
                                }
                            }
                        }
                        if (methodName.equals("setImageResource")) {
                            //setImageBitmap
                            mNew.add(new BitmapReflectionAction(viewId, "setImageBitmap",
                                    BitmapUtils.drawableToBitmap(appContext.getResources().getDrawable((int) value))));
                            mActions.remove(action);
                        } else if (methodName.equals("setText") && type == ReflectionActionCompat.INT) {
                            //setText string
                            Reflect.on(action).set("type", ReflectionActionCompat.STRING);
                            Reflect.on(action).set("value", appContext.getResources().getString((int) value));
                        } else if (methodName.equals("setLabelFor")) {
                            //TODO remove
                            mActions.remove(action);
                        } else if (methodName.equals("setBackgroundResource")) {
                            //TODO remove
                            mActions.remove(action);
                        } else if (methodName.equals("setImageURI")) {
                            Uri uri = (Uri) value;
                            if (!uri.getScheme().startsWith("http")) {
                                mActions.remove(action);
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (value instanceof Icon) {
                                    Icon icon = (Icon) value;
                                    fixIcon(icon, appContext, installed);
                                }
                            }
                        }
                    }
                }
                for (BitmapReflectionAction action : mNew) {
                    remoteViews.setBitmap(action.viewId, action.methodName, action.bitmap);
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mirror.vbox.widget.RemoteViews.mPackage.set(remoteViews, VCore.get().getHostPkg());
            }
        }
        return hasIcon;
    }

    void fixIconImage(Resources resources, RemoteViews remoteViews, boolean hasIconBitmap, Notification notification) {
        if (remoteViews == null || notification.icon == 0) return;
        if (!mNotificationCompat.isSystemLayout(remoteViews)) {
            return;
        }
        try {
            //noinspection deprecation
            int id = R_Hide.id.icon.get();
            //only fake small icon
            if (!hasIconBitmap && notification.largeIcon == null) {
                Drawable drawable = null;
                Bitmap bitmap = null;
                try {
                    drawable = resources.getDrawable(notification.icon);
                    drawable.setLevel(notification.iconLevel);
                    bitmap =  BitmapUtils.drawableToBitmap(drawable);
                }catch (Throwable e){
                    //no find
                }
                remoteViews.setImageViewBitmap(id, bitmap);
                //emui
                if(BuildCompat.isEMUI()) {
                    if (notification.largeIcon == null) {
                        notification.largeIcon = bitmap;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static class BitmapReflectionAction {
        int viewId;
        String methodName;
        Bitmap bitmap;

        BitmapReflectionAction(int viewId, String methodName, Bitmap bitmap) {
            this.viewId = viewId;
            this.methodName = methodName;
            this.bitmap = bitmap;
        }
    }
}
