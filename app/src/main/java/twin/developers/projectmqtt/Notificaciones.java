package twin.developers.projectmqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Notificaciones {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

        }
    }
}
