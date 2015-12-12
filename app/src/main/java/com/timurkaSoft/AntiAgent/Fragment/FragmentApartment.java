package com.timurkaSoft.AntiAgent.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFlat;
import com.timurkaSoft.AntiAgent.R;

public class FragmentApartment extends Fragment implements View.OnClickListener {

    private String apartment = "";
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apartment, container, false);

        setButton(R.id.buttonRooms0, view);
        setButton(R.id.buttonRooms1, view);
        setButton(R.id.buttonRooms2, view);
        setButton(R.id.buttonRooms3, view);
        setButton(R.id.buttonRooms4, view);

        this.view = view;
        return view;
    }

    private void setButton(int id, View view) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        button.setRippleColor(getResources().getColor(R.color.btn_on));
        button.setTextColor(getResources().getColor(R.color.btn_on));
        button.setOnClickListener(this);
    }

    private void activateButton(int id, View view) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        button.setBackgroundColor(getResources().getColor(R.color.btn_on));
        button.setTextColor(getResources().getColor(R.color.btn_off));
        button.setActivated(true);
    }

    private void deactivateButton(int id, View view) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        button.setBackgroundColor(getResources().getColor(R.color.btn_off));
        button.setTextColor(getResources().getColor(R.color.btn_on));
        button.setActivated(false);
    }

    private void setApartment(int id) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        if (!view.findViewById(id).isActivated()) {
            activateButton(id, view);
            apartment += "&roomsTotal=" + button.getTextView().getText();
            button.setRippleColor(getResources().getColor(R.color.btn_off));
        } else {
            button.setRippleColor(getResources().getColor(R.color.btn_on));
            deactivateButton(id, view);
            apartment = apartment.replace("&roomsTotal=" + button.getTextView().getText(), "");
        }
    }

    @Override
    public void onClick(View v) {
        setApartment(v.getId());
    }

    @Override
    public void onStop() {
        deactivateButton(R.id.buttonRooms0, view);
        deactivateButton(R.id.buttonRooms1, view);
        deactivateButton(R.id.buttonRooms2, view);
        deactivateButton(R.id.buttonRooms3, view);
        deactivateButton(R.id.buttonRooms4, view);
        apartment = "";
        super.onStop();
    }

    public String getRoomsTotal() {
        return apartment;
    }
}
