package ipvc.estg.wheretogo.Admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

import ipvc.estg.wheretogo.Classes.MyUser;
import ipvc.estg.wheretogo.Classes.ServiceLocation;
import ipvc.estg.wheretogo.Classes.SimpleCallback;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.R;

public class MapFragment extends Fragment {

    MapView mapView;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)          {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

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
                map.addMarker(new MarkerOptions().position(estg).title("ESTG"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(estg)
                        .zoom(18)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                createMarkers();
            }
        });

        return v;
    }


    public void createMarkers(){
        Utils.getAllTecnicosNome(new SimpleCallback() {
            @Override
            public void callback(Object data) {
                List<String> tecnicos = (List<String>) data;

                Utils.getLastLocations(tecnicos, new SimpleCallback() {
                    @Override
                    public void callback(Object data) {
                        Map<String, ServiceLocation> hashmap = (Map<String, ServiceLocation>)data;
                        map.clear();
                        for(Map.Entry<String, ServiceLocation> entry : hashmap.entrySet()){
                            String user = entry.getKey();
                            ServiceLocation sl = entry.getValue();

                            map.addMarker(new MarkerOptions().position(new LatLng(sl.getLatitude(),sl.getLongitude())).title(user));
                        }
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