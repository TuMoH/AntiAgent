package com.timurkaSoft.AntiAgent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuMoH on 20.06.2015.
 */
public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    TinyDB tinyDB = new TinyDB(context);
                    ArrayList<String> oldHref = tinyDB.getList("oldHref");
                    ArrayList<String> newHref = new ArrayList<>();

                    List<ShortInfo> elements = SiteParser.getShortInfoList(tinyDB.getString("updateUrl"));
                    for (ShortInfo element : elements) {
                        newHref.add(element.getHref());
                    }
                    if (oldHref.isEmpty() || newHref.isEmpty() || newHref.equals(oldHref))
                        return;

                    tinyDB.putList("oldHref", newHref);
                    sendNotification(context, elements, whatNews(oldHref, newHref));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int whatNews(List<String> oldList, List<String> newList) {
        int count = 0;
        for (String news : newList) {
            if (!oldList.contains(news))
                count++;
        }
        return count;
    }

    private void sendNotification(Context context, List<ShortInfo> elements, int count) {
        ArrayList<Parcelable> parcels = new ArrayList<>();
        for (ShortInfo element : elements) {
            parcels.add(Parcels.wrap(element));
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putParcelableArrayListExtra("news", parcels);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo_blue_orange)
                .setTicker("Антиагент. Есть новые объявления!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Антиагент")
                .setContentText("Добавлено " + count + " новых объявлений!");

        Notification n = builder.getNotification();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(101, n);
    }

}
