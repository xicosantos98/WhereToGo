package ipvc.estg.wheretogo.Classes;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import ipvc.estg.wheretogo.R;
import ipvc.estg.wheretogo.RouteHelper.MySingleton;
import ipvc.estg.wheretogo.Tecnico.TecMapFragment;

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


    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String getUrl(LatLng origin, LatLng dest, List<LatLng> waypoints, String directionMode, Context context) {

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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" +
                parameters + "&key=" + context.getString(R.string.api_key_routes);

        return url;
    }


    public static void getUserLogin(final String user, final SimpleCallback simpleCallback) {
        Query query = usersRef.orderByChild("nome").equalTo(user);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyUser u = null;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    u = d.getValue(MyUser.class);
                }
                simpleCallback.callback(u);
                //refUsers.child(u.getId()).child("location").setValue(new ServiceLocation(latitude,longitude));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getNumServicos(final String user, final SimpleCallback finishCallback) {
        LocalDateTime l = new LocalDateTime().minusDays(3);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd-MM-yyyy", l.toDate()).toString();


        Query query = serviceRef.orderByChild("data").equalTo(date);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numServicos = 0;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Servico s = d.getValue(Servico.class);
                        if (s.getTecnico().equals(user) && s.getEstado().equals(Estado.Pendente)) {
                            numServicos++;
                        }
                    }
                }

                finishCallback.callback(numServicos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public static void getAllTecnicos(final SimpleCallback simpleCallback) {

        final List<MyUser> tecnicos = new ArrayList<>();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    MyUser user = d.getValue(MyUser.class);

                    if (user.getTipo().equals(TipoUser.Tecnico)) {
                        tecnicos.add(user);
                    }
                }

                simpleCallback.callback(tecnicos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getAllServices(final MyUser user, final SimpleCallback simpleCallback) {
        LocalDateTime l = new LocalDateTime().minusDays(5);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd-MM-yyyy", l.toDate()).toString();


        Query query = serviceRef.orderByChild("data").equalTo(date);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Servico> servicos = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Servico servico = d.getValue(Servico.class);
                        if (servico.getTecnico().equals(user.getNome())) {
                            servicos.add(d.getValue(Servico.class));
                        }
                    }
                }

                simpleCallback.callback(servicos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getLastLocations(final List<String> users, final SimpleCallback simpleCallback) {

        Query query = usersRef.orderByChild("id");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, ServiceLocation> usersLocation = new HashMap<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    for (String user : users) {
                        if (d.getValue(MyUser.class).getNome().equals(user)) {
                            usersLocation.put(user, d.getValue(MyUser.class).getLocation());
                        }
                    }
                }
                simpleCallback.callback(usersLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getDistancesTime(String url, final Context context, final SimpleCallback simpleCallback) {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jRoutes;
                        JSONArray jLegs;
                        int duration = 0;
                        try {
                            jRoutes = response.getJSONArray("routes");

                            /** Traversing all routes */
                            for (int i = 0; i < jRoutes.length(); i++) {
                                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                                /** Traversing all legs */
                                for (int j = 0; j < jLegs.length(); j++) {
                                    JSONObject obj = jLegs.getJSONObject(j);
                                    JSONObject time = obj.getJSONObject("duration");
                                    duration += (time.getInt("value"));
                                }
                            }

                            simpleCallback.callback(duration);

                        } catch (JSONException e) {
                            simpleCallback.callback(null);
                            e.printStackTrace();
                        } catch (Exception e) {
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public static void usersMapDistance(JSONObject local, final List<ServiceLocation> locations, final List<String> users, final Context context, final SimpleCallback simpleCallback) {

        List<Integer> distancias = new ArrayList<>();
        Map<String,Integer> map = new HashMap<>();

        try {
            final double latitudeServico = local.getDouble("lat");
            final double longitudeServico = local.getDouble("lng");

            String origins = "";

            for (ServiceLocation sl : locations){
                origins += sl.getLatitude() + "," + sl.getLongitude() + "|";
            }


            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + origins +
                    "&destinations=" + latitudeServico + "," + longitudeServico + "&key=" + context.getString(R.string.api_key_routes);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray rows = response.getJSONArray("rows");
                                for (int i=0; i < rows.length(); i++){
                                    JSONArray elements = rows.getJSONObject(i).getJSONArray("elements");
                                    int distance = elements.getJSONObject(0).getJSONObject("distance").getInt("value");
                                    //distancias.add(distance);
                                    map.put(users.get(i), distance);
                                }
                                simpleCallback.callback(map);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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

            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendNotification(String morada, Context context, String id){

        String url = "https://fcm.googleapis.com/fcm/send";

        Map<String,Object> jsonParams = new HashMap<>();


        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("body", morada);
            data.put("title", "Novo Serviço");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, id);
            jsonParams.put("registration_ids",jsonArray);
            jsonParams.put("notification",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d("PARAMS", jsonParams.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, new JSONObject(jsonParams), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Notificado", Toast.LENGTH_SHORT).show();
                        Log.d("RESPOSTA", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", error.getMessage());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "key=AIzaSyCGmqnloJ8ZV_x6-_iv173uCfNN--05GuY");
                headers.put("User-agent", System.getProperty("http.agent"));
                headers.put("Connection", "close");
                return headers;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


}
