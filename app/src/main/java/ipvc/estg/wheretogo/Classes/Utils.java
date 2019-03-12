package ipvc.estg.wheretogo.Classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utils {
    public static String localizacaoName = "localizacao";
    public static String userName = "users";
    public static String serviceName = "servico";
    public static String servicetypeName = "tipo servico";
    public static DatabaseReference localizacaoRef = FirebaseDatabase.getInstance().getReference(Utils.localizacaoName);
    public static DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(Utils.userName);
    public static DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference(Utils.serviceName);
    public static DatabaseReference servicetypeRef = FirebaseDatabase.getInstance().getReference(Utils.servicetypeName);

    public static DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
}
