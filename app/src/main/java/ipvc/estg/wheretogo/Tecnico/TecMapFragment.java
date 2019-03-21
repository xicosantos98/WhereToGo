package ipvc.estg.wheretogo.Tecnico;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONObject;
import org.joda.time.*;
import org.joda.time.LocalDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ipvc.estg.wheretogo.Classes.MyUser;
import ipvc.estg.wheretogo.Classes.ServiceLocation;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.Login.LoginActivity;
import ipvc.estg.wheretogo.R;
import ipvc.estg.wheretogo.RouteHelper.DataParser;
import ipvc.estg.wheretogo.RouteHelper.MySingleton;
import ipvc.estg.wheretogo.RouteHelper.PointsParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class TecMapFragment extends Fragment implements OnMapReadyCallback {


    MapView mapView;
    GoogleMap map;
    DataParser parser;
    PointsParser pointsParser;
    List<List<HashMap<String, String>>> result;
    public static ArrayList<Integer> orderedWaypoints;
    private MarkerOptions pontoInicial = LoginActivity.pontoInicial;
    private MarkerOptions pontoFinal = LoginActivity.pontoFinal;
    private ArrayList<LatLng> waypoints;
    private Polyline currentPolyline;
    private PolylineOptions polylineOptions;
    private ArrayList<Servico> servicosTecnico;
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private String user;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tec_map, container, false);

        orderedWaypoints = new ArrayList<>();
        parser = new DataParser();
        pointsParser = new PointsParser();
        //user = ((TecMapActivity) getActivity()).getUser();
        user = LoginActivity.user;

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapViewTec);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        createPoints();
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);



                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(pontoInicial.getPosition())
                        .zoom(13)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        return v;
    }

    public void createPoints() {
        Query queryUser = usersRef.orderByChild("nome").equalTo(user);

        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyUser u = null;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    u = d.getValue(MyUser.class);
                }
                pontoInicial = new MarkerOptions()
                        .position(new LatLng(u.getLocation()
                                .getLatitude(), u.getLocation().getLongitude())).title("Início");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //pontoInicial = new MarkerOptions().position(new LatLng(41.702133, -8.848484)).title("Location 1");
        pontoFinal = new MarkerOptions().position(new LatLng(41.702133, -8.848484)).title("Location 2");

        LocalDateTime l = new LocalDateTime();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd-MM-yyyy", l.toDate()).toString();

        waypoints = new ArrayList<>();


        Query query = Utils.serviceRef
                .orderByChild("data")
                .equalTo(date);

        query.addListenerForSingleValueEvent(valueEventListener);
    }

    private String getUrl(LatLng origin, LatLng dest, ArrayList<LatLng> waypoints, String directionMode) {

        // Ponto inicial da rota
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Ponto final da rota
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Serviços (waypoints)
        String waypointsString = "waypoints=optimize:true|";

        for (LatLng coor : waypoints) {
            waypointsString += (coor.latitude + "," + coor.longitude + "|");
        }

        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + waypointsString + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.api_key_routes);
        return url;
    }


    public void createRoute(LatLng origin, LatLng dest, final ArrayList<LatLng> waypoints, String directionMode) {

        String url = getUrl(origin, dest, waypoints, directionMode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        result = parser.parse(response);
                        polylineOptions = pointsParser.getPolyline(result);
                        if (currentPolyline != null)
                            currentPolyline.remove();
                        currentPolyline = map.addPolyline(polylineOptions);

                        for (int i = 0; i < waypoints.size(); i++) {
                            map.addMarker(new MarkerOptions().position(waypoints.get(i)).title("Waypoint " +
                                    (orderedWaypoints.get(i) + 1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.service_pin)));
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", error.getMessage());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                headers.put("Connection", "close");
                return headers;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);


    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Servico service = d.getValue(Servico.class);

                    if (service.getTecnico().equals(user)) {
                        LatLng latLng = new LatLng(service.getCoordenadas().getLatitude(), service.getCoordenadas().getLongitude());
                        Log.d("TAG", "LATLNG: " + latLng.longitude);
                        waypoints.add(latLng);
                    }
                }


                createRoute(pontoInicial.getPosition(), pontoFinal.getPosition(), waypoints, "driving");
                map.addMarker(pontoInicial);
                map.addMarker(pontoFinal);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}
