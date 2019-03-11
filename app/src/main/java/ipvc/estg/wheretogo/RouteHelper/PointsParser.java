package ipvc.estg.wheretogo.RouteHelper;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser {

    public PolylineOptions getPolyline(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);

            lineOptions.width(10);
            lineOptions.color(Color.parseColor("#4285f4"));
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            return lineOptions;

        } else {
            return null;
        }
    }

}
