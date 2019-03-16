package ipvc.estg.wheretogo.Tecnico;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ipvc.estg.wheretogo.Adapter.ServiceAdapter;
import ipvc.estg.wheretogo.Adapter.ServiceAdapterTec;
import ipvc.estg.wheretogo.Classes.ILoadMore;
import ipvc.estg.wheretogo.Classes.Servico;
import ipvc.estg.wheretogo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TecServicesFragment extends Fragment {


    List<Servico> servicoList = new ArrayList<>();
    ServiceAdapterTec serviceAdapter;
    Servico s;
    RecyclerView recycler;
    DatabaseReference ref;
    String username;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tec_services, container, false);

        username = this.getArguments().getString("USER");

        ref = FirebaseDatabase.getInstance().getReference("servico");


        recycler = (RecyclerView) v.findViewById(R.id.rv_appointments_tec);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recycler.setLayoutManager(llm);


        initializeArray();

        return v;
    }

    public void initializeArray (){
        //SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

        LocalDateTime l = new LocalDateTime().minusDays(1);
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
                        if (s.getTecnico().equals(username))
                            servicoList.add(s);
                    }

                    serviceAdapter = new ServiceAdapterTec(recycler, getActivity(), servicoList);

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

                if(servicoList.size() != 0){
                    for (int i=0; i < servicoList.size(); i++){
                        if(servicoList.get(i).getId().equals(editServico.getId())){
                            servicoList.set(i, editServico);
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