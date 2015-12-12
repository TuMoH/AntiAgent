package com.timurkaSoft.AntiAgent.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.timurkaSoft.AntiAgent.R;

public class FragmentLot extends Fragment {

    EditText min, max;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lot, container, false);

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

    public String getLotArea() {
        String s = "";
        try {
        if (min.getText().toString().length() > 0)
            s += "&lotAreaMin=" + min.getText().toString();
        if (max.getText().toString().length() > 0)
            s += "&lotAreaMax=" + max.getText().toString();
        }
        catch (NullPointerException e) { /*NOP*/ }
        return s;
    }
}
