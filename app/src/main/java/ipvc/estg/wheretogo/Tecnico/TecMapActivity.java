package ipvc.estg.wheretogo.Tecnico;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import ipvc.estg.wheretogo.Classes.Localizacao;
import ipvc.estg.wheretogo.Classes.MyUser;
import ipvc.estg.wheretogo.Classes.ServiceLocation;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.SimpleCallback;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.Login.LoginActivity;
import ipvc.estg.wheretogo.R;

public class TecMapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Fragment fragment;
    BottomNavigationItemView itemView;
    String user, id_user;

    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;

    private LocationManager locationManager;
    private LocationListener locationListener;


    private DatabaseReference refLocation, refUsers;

    private double longitude;
    private double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tec_map);

        refLocation = FirebaseDatabase.getInstance().getReference("localizacao");
        refUsers = FirebaseDatabase.getInstance().getReference("users");


        Intent i = getIntent();
        //user = i.getStringExtra("USER");
        user = LoginActivity.user;
        id_user = i.getStringExtra("ID");
        getSupportActionBar().setTitle(" " + getString(R.string.str_technician) + ": " + user);
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

                        Bundle bundle = new Bundle();
                        bundle.putString("USER", user);
                        fragment = new TecServicesFragment();
                        fragment.setArguments(bundle);
                        break;

                    case R.id.navigation_map_tec:
                        fragment = new TecMapFragment();
                        break;
                    case R.id.navigation_logout_tec:
                        openDialog();
                        break;
                }

                if (fragment != null) {
                    return loadFragment(fragment);
                } else {
                    return true;
                }
            }
        });

        setUpLocation();

        loadFragment(new TecMapFragment());

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        itemView = (BottomNavigationItemView) v;

        View badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_badge, itemView, true);

        TextView textView = badge.findViewById(R.id.notifications_badge);


        LocalDateTime l = new LocalDateTime();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd-MM-yyyy", l.toDate()).toString();

        Query query = Utils.serviceRef
                .orderByChild("data")
                .equalTo(date);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int totServicos = 0;

                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Servico service = d.getValue(Servico.class);

                        if (service.getTecnico().equals(user)) {
                            totServicos ++;
                        }
                    }

                    textView.setText("" + totServicos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerTec, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public String getUser() {
        Intent i = getIntent();
        return i.getStringExtra("USER");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationListener();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationListener();
                displayLocation();
            }
        }
    }


    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            LocalDateTime l = new LocalDateTime();
            DateFormat df = new DateFormat();
            String date = df.format("dd-MM-yyyy", l.toDate()).toString();

            String id = refLocation.push().getKey();
            Localizacao localizacao = new Localizacao(id, new ServiceLocation(latitude, longitude), date, user);

            //refLocation.child(id).setValue(localizacao);

            Utils.getUserLogin(user, new SimpleCallback() {
                @Override
                public void callback(Object data) {
                    MyUser u = (MyUser)data;
                    refUsers.child(u.getId()).child("location").setValue(new ServiceLocation(latitude,longitude));
                }
            });

            Toast.makeText(this, "Latitude: " + latitude + " Longitude: " +longitude, Toast.LENGTH_SHORT).show();
            this.latitude = latitude;
            this.longitude = longitude;

        } else {
            Log.d("TAG", "Cant get your location");
        }
    }

    private void createLocationListener() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }


}
