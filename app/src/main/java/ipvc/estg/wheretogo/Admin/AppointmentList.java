package ipvc.estg.wheretogo.Admin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ipvc.estg.wheretogo.Adapter.ServiceAdapter;
import ipvc.estg.wheretogo.Classes.Estado;
import ipvc.estg.wheretogo.Classes.ILoadMore;
import ipvc.estg.wheretogo.Classes.ServiceLocation;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.Classes.TipoServico;
import ipvc.estg.wheretogo.Classes.Utils;
import ipvc.estg.wheretogo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentList extends Fragment {

    List<Servico> servicoList = new ArrayList<>();
    List<Servico> listaAtualizar = new ArrayList<>();
    Servico startingServico = null;
    View v;

    ServiceAdapter serviceAdapter;
    Servico s;
    RecyclerView recycler;
    DatabaseReference ref;





    public AppointmentList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =inflater.inflate(R.layout.fragment_appointment_list, container, false);

        ref = FirebaseDatabase.getInstance().getReference("servico");


        recycler = (RecyclerView) v.findViewById(R.id.rv_appointments);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recycler.setLayoutManager(llm);


        initializeArray();


        /*for (int i=0; i < 20; i++){
            String id = Utils.serviceRef.push().getKey();

            s=  new Servico(id, "Rua Manuel Bino", "Reparação Router", Estado.Pendente,
                    new ServiceLocation(42,42), new TipoServico("1", "Reparacao Router", 120), "32323332", "12-03-2019", "14:40", false, String.valueOf(i) );
            servicoList.add(s);
            FirebaseDatabase.getInstance().getReference("servico").child(id).setValue(s);
        }*/

        /*String id = Utils.serviceRef.push().getKey();

        s=  new Servico(id, "Rua Manuel Bino", "Reparação Router", Estado.Pendente,
                new ServiceLocation(42,42), new TipoServico("1", "Reparacao Router", 120), "32323332", "12-03-2019", "14:40", false, "diferente" );
        servicoList.add(s);
        FirebaseDatabase.getInstance().getReference("servico").child(id).setValue(s);*/


        return v;
    }

    public void initializeArray (){
        //SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

        LocalDateTime l = new LocalDateTime().minusDays(3);
        DateFormat df = new DateFormat();
        String date = df.format("dd-MM-yyyy", l.toDate()).toString();


        Query query = ref.orderByChild("data").equalTo(date);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //servicoList.clear();

                if(dataSnapshot.exists()){
                    for(DataSnapshot d: dataSnapshot.getChildren()){
                        Servico s = d.getValue(Servico.class);
                        servicoList.add(s);
                    }

                    listaAtualizar.addAll(servicoList.subList(0,10));

                    serviceAdapter = new ServiceAdapter(recycler, getActivity(), listaAtualizar);


                    serviceAdapter.setLoadMore(new ILoadMore() {
                        @Override
                        public void onLoadMore() {
                            if(listaAtualizar.size() < servicoList.size()){
                                listaAtualizar.add(null);
                                serviceAdapter.notifyItemInserted(listaAtualizar.size()-1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        listaAtualizar.remove(listaAtualizar.size()-1);
                                        serviceAdapter.notifyItemRemoved(listaAtualizar.size());
                                        int index = listaAtualizar.size();
                                        int end = index +3;
                                        for (int i=index; i < end && i < servicoList.size(); i++){
                                            listaAtualizar.add(servicoList.get(i));
                                        }
                                        serviceAdapter.notifyDataSetChanged();
                                        serviceAdapter.setLoading();
                                    }
                                }, 2000);
                            }else {
                                //Toast.makeText(getActivity(), "Data Loaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    recycler.setAdapter(serviceAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERRO AAAAAAAAAA", databaseError.getMessage());
            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(getActivity(), "Adicionei", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toast.makeText(getActivity(), "Alteração", Toast.LENGTH_SHORT).show();
                final Servico editServico = dataSnapshot.getValue(Servico.class);

                if(listaAtualizar.size() != 0){
                    for (int i=0; i < listaAtualizar.size(); i++){
                        if(listaAtualizar.get(i).getId().equals(editServico.getId())){
                            listaAtualizar.set(i, editServico);
                            serviceAdapter.notifyDataSetChanged();
                            return;
                        }
                    }

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
