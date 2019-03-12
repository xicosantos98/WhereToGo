package ipvc.estg.wheretogo.Admin;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipvc.estg.wheretogo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewAppointment extends Fragment {


    public NewAppointment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_appointment, container, false);
    }

}
