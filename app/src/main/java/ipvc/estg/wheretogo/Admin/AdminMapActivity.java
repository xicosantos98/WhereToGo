package ipvc.estg.wheretogo.Admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import ipvc.estg.wheretogo.R;

public class AdminMapActivity extends AppCompatActivity {
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.botto_navigation_admin);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_newAppoint :
                        fragment = new NewAppointment(); break;
                    case R.id.navigation_ListAppoint :
                        fragment = new AppointmentList(); break;
                    case R.id.navigation_map :
                        fragment = new MapFragment(); break;
                }
                return loadFragment(fragment);
            }
        });

        loadFragment(new AppointmentList());


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
}
