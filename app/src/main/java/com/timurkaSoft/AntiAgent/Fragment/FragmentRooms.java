package com.timurkaSoft.AntiAgent.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.timurkaSoft.AntiAgent.R;

public class FragmentRooms extends Fragment {

    EditText min, max;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        min = (EditText) view.findViewById(R.id.from);
        max = (EditText) view.findViewById(R.id.to);

        return view;
    }

    @Override
    public void onStop() {
        min.setText("");
        max.setText("");
        super.onStop();
    }

    public String getLivingSpace() {
        String s = "";
        try {
            if (min.getText().toString().length() > 0)
                s += "&livingSpaceMin=" + min.getText().toString();
            if (max.getText().toString().length() > 0)
                s += "&livingSpaceMax=" + max.getText().toString();
        }
        catch (NullPointerException e) { /*NOP*/ }
        return s;
    }
}
