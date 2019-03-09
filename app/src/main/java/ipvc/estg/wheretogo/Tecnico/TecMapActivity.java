package ipvc.estg.wheretogo.Tecnico;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import ipvc.estg.wheretogo.R;

public class TecMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tec_map);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.botto_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home :
                        Toast.makeText(TecMapActivity.this, "Navigation 1", Toast.LENGTH_SHORT).show();break;
                }
                return true;
            }
        });
    }
}
