package ipvc.estg.wheretogo.Admin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.Login.LoginActivity;
import ipvc.estg.wheretogo.R;
import ipvc.estg.wheretogo.Tecnico.TecMapActivity;

public class AdminMapActivity extends AppCompatActivity {
    Fragment fragment;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);

        Activity a = this;


        relativeLayout = findViewById(R.id.activity_admin);
        getSupportActionBar().setTitle(" Administrador");
        getSupportActionBar().setLogo(R.drawable.ic_businessman);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.botto_navigation_admin);
        bottomNavigationView.setSelectedItemId(R.id.navigation_ListAppoint);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_newAppoint :
                        if (!Utils.isNetworkAvailable(a)) {
                            Snackbar.make(relativeLayout.getRootView(), R.string.str_no_connection, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        fragment = new NewAppointment(); break;
                    case R.id.navigation_ListAppoint :
                        if (!Utils.isNetworkAvailable(a)) {
                            Snackbar.make(relativeLayout.getRootView(), R.string.str_no_connection, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        fragment = new AppointmentList(); break;
                    case R.id.navigation_map :
                        if (!Utils.isNetworkAvailable(a)) {
                            Snackbar.make(relativeLayout.getRootView(), R.string.str_no_connection, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        fragment = new MapFragment(); break;
                    case R.id.navigation_logout:
                        openDialog();
                        break;
                }
                return loadFragment(fragment);
            }
        });

        loadFragment(new AppointmentList());


    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMapActivity.this, R.style.AlertDialogStyle);
        builder.setMessage("Deseja terminar sessão ?");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AdminMapActivity.this, LoginActivity.class);
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
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        FirebaseAuth.getInstance().signOut();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }
}

