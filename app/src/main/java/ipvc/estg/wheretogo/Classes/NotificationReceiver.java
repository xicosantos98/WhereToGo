package ipvc.estg.wheretogo.Classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String acception = intent.getExtras().getString("Aceitacao");
        int id = intent.getExtras().getInt("ID");


        if (acception.equals("Sim")){
            Toast.makeText(context, "Aceite", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Recusar", Toast.LENGTH_SHORT).show();
        }

        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }
}
