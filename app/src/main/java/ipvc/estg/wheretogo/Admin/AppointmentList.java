package ipvc.estg.wheretogo.Admin;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    Servico startingServico = null;

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
        View v =inflater.inflate(R.layout.fragment_appointment_list, container, false);
        ref = FirebaseDatabase.getInstance().getReference("servico");
        //ref.keepSynced(true);



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
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

        LocalDateTime l = new LocalDateTime().minusDays(1);
        DateFormat df = new DateFormat();
        String date = df.format("dd-MM-yyyy", l.toDate()).toString();


        Query query = ref
                .orderByChild("data");
                /*.limitToFirst(11)
                .startAt(date, "-L_oTA6G0v6IRON7wPKr")
                .endAt(date)*/
                //query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getActivity(), "Atualizei", Toast.LENGTH_SHORT).show();
                if(dataSnapshot.exists()){
                    for(DataSnapshot d: dataSnapshot.getChildren()){
                        Servico s = d.getValue(Servico.class);
                        servicoList.add(s);
                    }
                    startingServico = servicoList.get(servicoList.size()-1);
                    servicoList.remove(servicoList.size()-1);

                    serviceAdapter = new ServiceAdapter(recycler, getActivity(), servicoList);

                    recycler.setAdapter(serviceAdapter);

                    /*serviceAdapter.setLoadMore(new ILoadMore() {
                        @Override
                        public void onLoadMore() {
                            if(servicoList.size()<=30){
                                servicoList.add(null);
                                serviceAdapter.notifyItemInserted(servicoList.size()-1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        servicoList.remove(servicoList.size()-1);
                                        serviceAdapter.notifyItemRemoved(servicoList.size());
                                        int index = servicoList.size();
                                        int end = index +3;
                                        for (int i=index; i < end; i++){
                                            servicoList.add(s);
                                        }
                                        serviceAdapter.notifyDataSetChanged();
                                        serviceAdapter.setLoading();
                                    }
                                }, 5000);
                            }else {
                                Toast.makeText(getActivity(), "Data Loaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERRO AAAAAAAAAA", databaseError.getMessage());
            }
        });
    }

}
