package ipvc.estg.wheretogo.Admin;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
        View v = inflater.inflate(R.layout.fragment_new_appointment, container, false);

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner_moradas);
        Spinner spinnerTipo = (Spinner) v.findViewById(R.id.spinner_tipo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.countries_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                getActivity(), R.array.types_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

        spinnerTipo.setAdapter(adapterTipo);

        return v;
    }


    public  class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Toast.makeText(parent.getContext(), "Item is " +
                    parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }

}
