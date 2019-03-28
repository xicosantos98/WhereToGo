package ipvc.estg.wheretogo.Admin;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.common.FirstPartyScopes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ipvc.estg.wheretogo.Classes.Estado;
import ipvc.estg.wheretogo.Classes.MyUser;
import ipvc.estg.wheretogo.Classes.ServiceLocation;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.SimpleCallback;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.Login.LoginActivity;
import ipvc.estg.wheretogo.R;
import ipvc.estg.wheretogo.RouteHelper.DataParser;
import ipvc.estg.wheretogo.RouteHelper.DataParserAll;
import ipvc.estg.wheretogo.RouteHelper.MySingleton;
import ipvc.estg.wheretogo.RouteHelper.PointsParser;

import static ipvc.estg.wheretogo.Login.LoginActivity.user;

public class MapFragment extends Fragment {

    MapView mapView;
    GoogleMap map;
    private MarkerOptions pontoFinal = LoginActivity.pontoFinal;
    DataParserAll parser;
    PointsParser pointsParser;
    PolylineOptions polylineOptions;
    List<List<HashMap<String, String>>> result;
    ArrayList<Integer> markers = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    Map<String, Integer> mapColors = new HashMap<>();
    Map<String, Integer> mapMarkers = new HashMap<>();
    Map<String, LatLng> mapLocations = new HashMap<>();
    Map<String, PolylineOptions> mapPolylines = new HashMap<>();


    ArrayList<LatLng> waypoints = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        parser = new DataParserAll();
        pointsParser = new PointsParser();
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        markers.add(R.drawable.azul);
        markers.add(R.drawable.azul2);
        markers.add(R.drawable.verde);
        markers.add(R.drawable.laranja);
        markers.add(R.drawable.vermelho);
        markers.add(R.drawable.cinzento);

        colors.add(R.color.tec_azul);
        colors.add(R.color.tec_azul2);
        colors.add(R.color.tec_verde);
        colors.add(R.color.tec_laranja);
        colors.add(R.color.tec_vermelho);
        colors.add(R.color.tec_cinzento);


        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                LatLng estg = new LatLng(41.693455, -8.846676);
                //map.addMarker(new MarkerOptions().position(estg).title("ESTG"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(estg)
                        .zoom(18)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                createMarkers();


                Utils.getAllTecnicosNome(new SimpleCallback() {
                    @Override
                    public void callback(Object data) {
                        List<String> users = (List<String>) data;

                        String[] arr = users.toArray(new String[0]);

                        new RoutesTask(getActivity(), mapColors, mapLocations, mapMarkers, map).execute(arr);

                    }
                });

            }
        });

        return v;
    }



    public void createMarkers() {
        Utils.getAllTecnicosNome(new SimpleCallback() {
            @Override
            public void callback(Object data) {
                List<String> tecnicos = (List<String>) data;

                Utils.getLastLocations(tecnicos, new SimpleCallback() {
                    @Override
                    public void callback(Object data) {
                        int i = 0;
                        Map<String, ServiceLocation> hashmap = (Map<String, ServiceLocation>) data;
                        map.clear();
                        for (Map.Entry<String, ServiceLocation> entry : hashmap.entrySet()) {
                            String user = entry.getKey();
                            ServiceLocation sl = entry.getValue();
                            mapMarkers.put(user, markers.get(i));
                            mapColors.put(user, colors.get(i));
                            mapLocations.put(user, new LatLng(sl.getLatitude(), sl.getLongitude()));
                            map.addMarker(new MarkerOptions().position(new LatLng(sl.getLatitude(), sl.getLongitude())).title("Inicio de rota: " + user).icon(BitmapDescriptorFactory.fromResource(mapMarkers.get(user))));
                            i++;
                        }
                        map.addMarker(pontoFinal.title(getResources().getString(R.string.str_end_rout)));
                    }
                });

            }

        });
    }



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

}


class RoutesTask extends AsyncTask<String, Void, Map<String, PolylineOptions>> {

    public Activity activity;
    public DataParserAll parser;
    public PointsParser pointsParser;
    public PolylineOptions polylineOptions;
    public List<List<HashMap<String, String>>> result;
    Map<String, Integer> mapColors = new HashMap<>();
    Map<String, LatLng> mapLocations = new HashMap<>();
    Map<String, Integer> mapMarkers = new HashMap<>();

    GoogleMap map;

    public RoutesTask(Activity activity,Map<String, Integer> mapColors,Map<String, LatLng> mapLocations,Map<String, Integer> mapMarkers,  GoogleMap map) {
        this.activity = activity;
        this.mapColors = mapColors;
        this.mapLocations = mapLocations;
        this.map = map;
        this.mapMarkers = mapMarkers;
        parser = new DataParserAll();
        pointsParser = new PointsParser();
    }

    @Override
    protected Map<String, PolylineOptions> doInBackground(String... strings) {

        Map<String, PolylineOptions> usersRoute = new HashMap<>();

        for (int i=0 ; i < strings.length; i++){

            LocalDateTime l = new LocalDateTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String date = df.format("dd-MM-yyyy", l.toDate()).toString();
            ArrayList<LatLng> waypoints = new ArrayList<>();
            int count = i;


            Query query = Utils.serviceRef
                    .orderByChild("data")
                    .equalTo(date);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            Servico service = d.getValue(Servico.class);

                            if (service.getTecnico().equals(strings[count]) && (service.getEstado().equals(Estado.Concluido) || service.getEstado().equals(Estado.Pendente))) {
                                LatLng latLng = new LatLng(service.getCoordenadas().getLatitude(), service.getCoordenadas().getLongitude());
                                waypoints.add(latLng);
                            }
                        }

                        createRoute(mapLocations.get(strings[count]), LoginActivity.pontoFinal.getPosition(), waypoints, "driving", strings[count], usersRoute);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        return usersRoute;
    }

    @Override
    protected void onPostExecute(Map<String, PolylineOptions> stringPolylineOptionsMap) {
        //Toast.makeText(activity, stringPolylineOptionsMap.toString(), Toast.LENGTH_SHORT).show();
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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + activity.getString(R.string.api_key_routes);
        return url;
    }

    public void createRoute(LatLng origin, LatLng dest, final ArrayList<LatLng> waypoints, String directionMode, String user, Map<String, PolylineOptions> mapUsers) {

        String url = getUrl(origin, dest, waypoints, directionMode);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        result = parser.parse(response);

                        if (result != null) {
                            polylineOptions = pointsParser.getPolyline(result);
                        }

                        if (polylineOptions != null) {
                            polylineOptions.color(activity.getColor(mapColors.get(user)));
                        }

                        for (int i = 0; i < waypoints.size(); i++) {
                            map.addMarker(new MarkerOptions().position(waypoints.get(i)).title("Servico do tecnico: " + user).icon(BitmapDescriptorFactory.fromResource(mapMarkers.get(user))));
                        }


                        map.addPolyline(polylineOptions);

                        mapUsers.put(user, polylineOptions);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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


        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);


    }
}