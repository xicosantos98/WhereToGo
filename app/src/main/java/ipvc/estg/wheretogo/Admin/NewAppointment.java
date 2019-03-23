package ipvc.estg.wheretogo.Admin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.FragmentTransaction;
import ipvc.estg.wheretogo.Classes.Estado;
import ipvc.estg.wheretogo.Classes.Localizacao;
import ipvc.estg.wheretogo.Classes.MapUtil;
import ipvc.estg.wheretogo.Classes.MyUser;
import ipvc.estg.wheretogo.Classes.ServiceLocation;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.SimpleCallback;
import ipvc.estg.wheretogo.Classes.TipoServico;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.Login.LoginActivity;
import ipvc.estg.wheretogo.R;
import ipvc.estg.wheretogo.RouteHelper.AddressParser;
import ipvc.estg.wheretogo.RouteHelper.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewAppointment extends Fragment {

    Button btnSearch, btnErase, btnConfirm;
    EditText address;
    EditText description;
    HashMap<String, JSONObject> result;
    Spinner spinnerMoradas, spinnerTipo, spinnerTecnicos;
    DatabaseReference tipos = FirebaseDatabase.getInstance().getReference("tipo_servico");
    DatabaseReference servicos = FirebaseDatabase.getInstance().getReference("servico");
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

    private MarkerOptions pontoInicial = LoginActivity.pontoInicial;
    private MarkerOptions pontoFinal = LoginActivity.pontoFinal;


    private List<LatLng> waypoints = new ArrayList<>();
    private int totServicosPendentes = 0;
    private double horasServico = 0;
    private int totTime = 0;

    //List<TipoServico> tiposList = new ArrayList<>();
    Map<String, TipoServico> tiposList = new HashMap<>();
    Map<String, Integer> userLocDistance = new HashMap<>();
    List<String> availableUsers = new ArrayList<>();
    List<String> orderedUsers = new ArrayList<>();
    List<Localizacao> locals = new ArrayList<>();
    Fragment fragment;

    LinearLayout service, buttons;


    public NewAppointment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_appointment, container, false);
        fragment = this;

        btnSearch = v.findViewById(R.id.btn_search_address);
        btnErase = v.findViewById(R.id.btn_clean_address);
        btnConfirm = v.findViewById(R.id.btn_insert_service);
        address = v.findViewById(R.id.input_address);
        description = v.findViewById(R.id.text_description);
        description.setImeOptions(EditorInfo.IME_ACTION_DONE);
        description.setRawInputType(InputType.TYPE_CLASS_TEXT);

        service = v.findViewById(R.id.linear_hide);
        buttons = v.findViewById(R.id.linear_buttons);

        spinnerMoradas = (Spinner) v.findViewById(R.id.spinner_moradas);
        spinnerTipo = (Spinner) v.findViewById(R.id.spinner_tipo);
        spinnerTecnicos = (Spinner) v.findViewById(R.id.spinner_tecnico);

        address.setText("Rua Manuel Espregueira");
        setAvailableUsers();


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRequest(address.getText().toString());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                spinnerMoradas.requestFocus();
            }
        });


        btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address.setText("");
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject serviceLocation = result.get(spinnerMoradas.getSelectedItem().toString());
                try {

                    Double latitude = (serviceLocation.getDouble("lat"));
                    Double longitude = (serviceLocation.getDouble("lng"));

                    LocalDateTime l = new LocalDateTime();
                    DateFormat df = new DateFormat();
                    String date = df.format("dd-MM-yyyy", l.toDate()).toString();

                    /*String id = servicos.push().getKey();
                    Servico s = new Servico(id, spinnerMoradas.getSelectedItem().toString(), description.getText().toString(), Estado.Pendente,
                            new ServiceLocation(latitude, longitude), tiposList.get(spinnerTipo.getSelectedItem().toString()),
                            "32323332", date,
                            spinnerTecnicos.getSelectedItem().toString());

                    servicos.child(id).setValue(s);*/

                    Query queryUser = usersRef.orderByChild("nome").equalTo(spinnerTecnicos.getSelectedItem().toString());

                    queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            MyUser u = null;
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                u = d.getValue(MyUser.class);
                            }
                            //Utils.sendNotification(spinnerMoradas.getSelectedItem().toString(), getActivity(), u.getToken());

                            Snackbar
                                    .make(getView(), getResources().getString(R.string.str_notify),
                                            Snackbar.LENGTH_LONG)
                                    .setAction(getResources().getString(R.string.str_watch_list), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent;
                                            intent = new Intent(getActivity(), AdminMapActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragment).attach(fragment).commit();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    btnSearch.setVisibility(View.VISIBLE);
                } else {
                    btnSearch.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        tipos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    TipoServico t = d.getValue(TipoServico.class);
                    //tiposList.add(t);
                    tiposList.put(t.getNome(), t);
                }

                ArrayAdapter<String> adapterTipo = new ArrayAdapter<String>(getActivity(),
                        R.layout.spinner_item, new ArrayList<String>(tiposList.keySet()));
                adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTipo.setAdapter(adapterTipo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return v;
    }


    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            service.setVisibility(View.VISIBLE);
            orderArray();
            buttons.setVisibility(View.VISIBLE);

        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }


    public void createRequest(String search) {

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + search +
                "&region=pt&key=" + getString(R.string.str_api_geo);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AddressParser addressParser = new AddressParser();
                        result = addressParser.getInfo(response);
                        List<String> addresses = new ArrayList<>();
                        addresses.addAll(result.keySet());

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, addresses);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerMoradas.setAdapter(adapter);
                        spinnerMoradas.setOnItemSelectedListener(new MyOnItemSelectedListener());
                        spinnerMoradas.setSelection(0);

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

    public void setAvailableUsers() {
        Utils.getAllTecnicos(new SimpleCallback() {
            @Override
            public void callback(Object data) {
                List<MyUser> tecnicos = (List<MyUser>) data;

                for (final MyUser user : tecnicos) {
                    Utils.getAllServices(user, new SimpleCallback() {
                        @Override
                        public void callback(Object data) {

                            List<Servico> servicos = (List<Servico>) data;
                            waypoints = new ArrayList<>();
                            totServicosPendentes = 0;
                            horasServico = 0;
                            totTime = 0;

                            if (servicos.isEmpty()) {
                                availableUsers.add(user.getNome());
                            }

                            for (Servico s : servicos) {
                                if (s.getEstado().equals(Estado.Pendente)) {
                                    totServicosPendentes++;
                                    horasServico += s.getTipo().getTempoDuracao();
                                    waypoints.add(new LatLng(s.getCoordenadas().getLatitude(), s.getCoordenadas().getLongitude()));
                                } else if (s.getEstado().equals(Estado.Concluido)) {
                                    horasServico += s.getTipo().getTempoDuracao();
                                    waypoints.add(new LatLng(s.getCoordenadas().getLatitude(), s.getCoordenadas().getLongitude()));
                                }
                            }

                            if (!waypoints.isEmpty()) {
                                String url = Utils.getUrl(pontoInicial.getPosition(), pontoFinal.getPosition(), waypoints, "driving", getActivity());
                                Utils.getDistancesTime(url, getActivity(), new SimpleCallback() {
                                    @Override
                                    public void callback(Object data) {
                                        totTime = (Integer) data;
                                        totTime = totTime / 60;

                                        horasServico += totTime;
                                        horasServico = horasServico / 60;

                                        if (totServicosPendentes <= 3 && horasServico <= 7) {
                                            availableUsers.add(user.getNome());
                                        }

                                        orderArray();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


    public void orderArray() {


        String selectedMorada = (String) spinnerMoradas.getSelectedItem();
        Utils.getLastLocations(availableUsers, new SimpleCallback() {
            @Override
            public void callback(Object data) {
                Map<String, ServiceLocation> map = (Map<String, ServiceLocation>) data;
                List<ServiceLocation> locations = new ArrayList<ServiceLocation>(map.values());
                List<String> users = new ArrayList<String>(map.keySet());

                Utils.usersMapDistance(result.get(selectedMorada), locations, users, getActivity(), new SimpleCallback() {
                    @Override
                    public void callback(Object data) {
                        Map<String, Integer> userDistancias = (Map<String, Integer>) data;
                        Map<String, Integer> orderedMap = MapUtil.sortByValue(userDistancias);
                        availableUsers = new ArrayList<>(orderedMap.keySet());
                        Log.d("NAO ORDENADO", userDistancias.toString());
                        Log.d("ORDENADO", orderedMap.toString());
                        ArrayAdapter<String> adapterUser = new ArrayAdapter<String>(getActivity(),
                                R.layout.spinner_item, availableUsers);
                        adapterUser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTecnicos.setAdapter(adapterUser);
                    }
                });

            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerTec, fragment)
                    .commit();
            return true;
        }
        return false;
    }


}
