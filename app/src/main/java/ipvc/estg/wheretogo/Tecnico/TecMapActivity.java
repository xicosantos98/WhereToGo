package ipvc.estg.wheretogo.Tecnico;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import ipvc.estg.wheretogo.Admin.MapFragment;
import ipvc.estg.wheretogo.Login.LoginActivity;
import ipvc.estg.wheretogo.R;

public class TecMapActivity extends AppCompatActivity {

    Fragment fragment;
    BottomNavigationItemView itemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tec_map);



        Intent i = getIntent();
        getSupportActionBar().setTitle(" Técnico: " + i.getStringExtra("USER"));
        getSupportActionBar().setLogo(R.drawable.ic_account_circle_black_24dp);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        BottomNavigationView bottomNavigationView = findViewById(R.id.botto_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_map_tec);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_ListAppoint_tec:
                        Toast.makeText(TecMapActivity.this, "Servicos", Toast.LENGTH_SHORT).show();
                        //itemView.removeViewAt(2);
                        break;
                    case R.id.navigation_map_tec:
                        fragment = new TecMapFragment(); break;
                    case R.id.navigation_logout_tec:
                        openDialog();
                        break;
                }

                if(fragment != null){
                    return loadFragment(fragment);
                }else{
                    return true;
                }
            }
        });

        loadFragment(new TecMapFragment());

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
         itemView = (BottomNavigationItemView) v;

        View badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_badge, itemView, true);

        TextView textView = badge.findViewById(R.id.notifications_badge);
        textView.setText("3");
    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TecMapActivity.this, R.style.AlertDialogStyle);
        builder.setMessage("Deseja terminar sessão ?");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(TecMapActivity.this, LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerTec, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
