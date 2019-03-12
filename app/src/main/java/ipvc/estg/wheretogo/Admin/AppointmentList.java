package ipvc.estg.wheretogo.Admin;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import ipvc.estg.wheretogo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentList extends Fragment {

    List<Servico> servicoList = new ArrayList<>();

    ServiceAdapter serviceAdapter;
    Servico s;





    public AppointmentList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_appointment_list, container, false);

       s=  new Servico("1", "Rua Manuel Bino", "Reparação Router", Estado.Pendente,
                new ServiceLocation(42,42), new TipoServico("1", "Reparacao Router", 120), "32323332", "12-03-2019", "14:40", false, "José Silva" );

        for (int i=0; i < 10; i++){
            servicoList.add(s);
        }

        servicoList.

        RecyclerView recycler = (RecyclerView) v.findViewById(R.id.rv_appointments);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recycler.setLayoutManager(llm);

        serviceAdapter = new ServiceAdapter(recycler, getActivity(), servicoList);

        recycler.setAdapter(serviceAdapter);

        serviceAdapter.setLoadMore(new ILoadMore() {
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
        });


        return v;
    }

}
