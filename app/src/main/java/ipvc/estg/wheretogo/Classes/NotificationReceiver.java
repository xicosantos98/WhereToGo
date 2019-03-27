package ipvc.estg.wheretogo.Classes;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ipvc.estg.wheretogo.Tecnico.TecMapActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String acception = intent.getExtras().getString("Aceitacao");
        int id = intent.getExtras().getInt("ID");
        String idServico = intent.getExtras().getString("ID_SERVICO");
        DatabaseReference servicos = FirebaseDatabase.getInstance().getReference("servico");
        Estado e;


        if (acception.equals("Sim")){
            //Toast.makeText(context, "Aceite", Toast.LENGTH_SHORT).show();
            Intent refresh = new Intent(context, TecMapActivity.class);
            context.startActivity(refresh);
            e = Estado.Pendente;
        }else{
            //Toast.makeText(context, "Recusar", Toast.LENGTH_SHORT).show();
            e = Estado.Rejeitado;
        }


        servicos.child(idServico).child("estado").setValue(e);

        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }
}
