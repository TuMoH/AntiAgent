package com.timurkaSoft.AntiAgent.HeadFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gc.materialdesign.views.ButtonFlat;
import com.timurkaSoft.AntiAgent.Fragment.FragmentApartment;
import com.timurkaSoft.AntiAgent.Fragment.FragmentLot;
import com.timurkaSoft.AntiAgent.Fragment.FragmentRooms;
import com.timurkaSoft.AntiAgent.Fragment.FragmentVoid;
import com.timurkaSoft.AntiAgent.MainActivity;
import com.timurkaSoft.AntiAgent.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadFragmentSearch extends Fragment implements View.OnClickListener {

    private String[] dataCity = new String[]{"Краснодар", "Москва", "Самара", "Воронеж", "Пермь", "Екатеринбург", "Ростов-на-Дону",
            "Казань", "Санкт-Петербург", "Нижний Новгород", "Уфа", "Челябинск", "Новосибирск"};
    public static String[] dataCityTag = new String[]{"krd", "msk", "smr", "vrn", "prm", "ekb", "rnd", "kzn", "spb", "nnov", "ufa", "chel", "nsk"};
    private String currentCity;
    private List<String> dataZone = new ArrayList<>();
    private List<String> dataZoneTag = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapterZone;
    private String type = "SELL";
    private String category = "APARTMENT";

    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private FragmentApartment fragmentApartment = new FragmentApartment();
    private FragmentRooms fragmentRooms = new FragmentRooms();
    private FragmentLot fragmentLot = new FragmentLot();
    private FragmentVoid fragmentVoid = new FragmentVoid();

    Spinner spinnerCity, spinnerZone;
    EditText priceMin, priceMax;
    boolean firstCreate = true;
    private boolean onTop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.head_fragment_search, container, false);

        setButton(R.id.buttonSELL, view);
        setButton(R.id.buttonRENT, view);

        setButton(R.id.buttonAPARTMENT, view);
        setButton(R.id.buttonROOMS, view);
        setButton(R.id.buttonHOUSE, view);
        setButton(R.id.buttonGARAGE, view);
        setButton(R.id.buttonCOMMERCIAL, view);
        setButton(R.id.buttonLOT, view);

        if (firstCreate) {
            onTop = true;
            activateButton(R.id.buttonSELL, view);
            activateButton(R.id.buttonAPARTMENT, view);
        }

        priceMin = (EditText) view.findViewById(R.id.from);
        priceMax = (EditText) view.findViewById(R.id.to);

        ArrayAdapter<String> spinnerAdapterCity = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dataCity);

        currentCity = dataCityTag[MainActivity.tinydb.getInt("currentCity")];
        spinnerCity = (Spinner) view.findViewById(R.id.spinnerCity);
        spinnerAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(spinnerAdapterCity);
        spinnerCity.setSelection(MainActivity.tinydb.getInt("currentCity"));
        spinnerCity.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentCity = dataCityTag[position];
                MainActivity.tinydb.putInt("currentCity", position);
                dataZone = MainActivity.tinydb.getList("dataZone" + currentCity);
                dataZoneTag = MainActivity.tinydb.getList("dataZoneTag" + currentCity);
                spinnerAdapterZone = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dataZone);
                spinnerAdapterZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAdapterZone.notifyDataSetChanged();
                spinnerZone.setAdapter(spinnerAdapterZone);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        ArrayList<String> newDataZone = MainActivity.tinydb.getList("dataZone" + currentCity);
        ArrayList<String> newDataZoneTag = MainActivity.tinydb.getList("dataZoneTag" + currentCity);
        if (newDataZone.size() > 0 && newDataZoneTag.size() > 0) {
            dataZone = newDataZone;
            dataZoneTag = newDataZoneTag;
        } else {
            setDefaultDataCity();
            dataZone = MainActivity.tinydb.getList("dataZone" + currentCity);
            dataZoneTag = MainActivity.tinydb.getList("dataZoneTag" + currentCity);
        }

        spinnerAdapterZone = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dataZone);
        spinnerZone = (Spinner) view.findViewById(R.id.spinnerZone);
        spinnerAdapterZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZone.setAdapter(spinnerAdapterZone);

        if (firstCreate) {
            fragmentManager = getFragmentManager();
            currentFragment = fragmentApartment;
            addTransition(fragmentApartment);
            firstCreate = false;
        }

//        RangeBar rangebar = (RangeBar) view.findViewById(R.id.priceRangebar);
//        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
//            @Override
//            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
//                                              int rightPinIndex,
//                                              String leftPinValue, String rightPinValue) {
//                priceMin.setText("" + leftPinIndex);
//                priceMax.setText("" + rightPinIndex);
//            }
//
//        });
        return view;
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        ViewGroup viewGroup = (ViewGroup) getView();
//        viewGroup.removeAllViewsInLayout();
//        View view = onCreateView(getActivity().getLayoutInflater(), viewGroup, null);
//        viewGroup.addView(view);
//        addTransition(fragmentVoid);
//        restartButtons();
//    }

    public void setOnTop(boolean onTop) {
        this.onTop = onTop;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSELL:
                setType(R.id.buttonSELL);
                break;
            case R.id.buttonRENT:
                setType(R.id.buttonRENT);
                break;
            case R.id.buttonAPARTMENT:
                setCategory(R.id.buttonAPARTMENT);
                break;
            case R.id.buttonROOMS:
                setCategory(R.id.buttonROOMS);
                break;
            case R.id.buttonHOUSE:
                setCategory(R.id.buttonHOUSE);
                break;
            case R.id.buttonGARAGE:
                setCategory(R.id.buttonGARAGE);
                break;
            case R.id.buttonCOMMERCIAL:
                setCategory(R.id.buttonCOMMERCIAL);
                break;
            case R.id.buttonLOT:
                setCategory(R.id.buttonLOT);
                break;
        }
    }

    private void addTransition(Fragment nextFragment) {
        try {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.scalex_enter, R.animator.scalex_exit, R.animator.scalex_enter, R.animator.scalex_exit)
                    .replace(R.id.container, nextFragment)
                    .commit();
            currentFragment = nextFragment;
        } catch (Exception e) {/* no transaction */}
    }

    private void setButton(int id, View view) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        button.setRippleColor(getResources().getColor(R.color.btn_on));
        button.setTextColor(getResources().getColor(R.color.btn_on));
        button.setOnClickListener(this);
    }

    private void activateButton(int id, View view) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        button.setBackgroundColor(button.getRippleColor());
        button.setTextColor(getResources().getColor(R.color.btn_off));
        button.setEnabled(false);
    }

    private void deactivateButton(int id, View view) {
        ButtonFlat button = (ButtonFlat) view.findViewById(id);
        button.setBackgroundColor(getResources().getColor(R.color.btn_off));
        button.setTextColor(getResources().getColor(R.color.btn_on));
        button.setEnabled(true);
    }

    private void setType(int id) {
        deactivateButton(R.id.buttonSELL, getView());
        deactivateButton(R.id.buttonRENT, getView());
        activateButton(id, getView());
        switch (id) {
            case R.id.buttonSELL:
                type = "SELL";
                break;
            case R.id.buttonRENT:
                type = "RENT";
                break;
        }
    }

    private void setCategory(int id) {
        deactivateButton(R.id.buttonAPARTMENT, getView());
        deactivateButton(R.id.buttonROOMS, getView());
        deactivateButton(R.id.buttonHOUSE, getView());
        deactivateButton(R.id.buttonGARAGE, getView());
        deactivateButton(R.id.buttonCOMMERCIAL, getView());
        deactivateButton(R.id.buttonLOT, getView());
        activateButton(id, getView());
        switch (id) {
            case R.id.buttonAPARTMENT:
                category = "APARTMENT";
                break;
            case R.id.buttonROOMS:
                category = "ROOMS";
                break;
            case R.id.buttonHOUSE:
                category = "HOUSE";
                break;
            case R.id.buttonGARAGE:
                category = "GARAGE";
                break;
            case R.id.buttonCOMMERCIAL:
                category = "COMMERCIAL";
                break;
            case R.id.buttonLOT:
                category = "LOT";
                break;
        }
        setFragment(category);
    }

    public void setFragment(String name) {
        if (name.equals("onTop")) {
            name = category;
            setOnTop(true);
        }

        if (onTop) {
            switch (name) {
                case "APARTMENT":
                    addTransition(fragmentApartment);
                    break;
                case "ROOMS":
                    addTransition(fragmentRooms);
                    break;
                case "LOT":
                    addTransition(fragmentLot);
                    break;
                default:
                    if (currentFragment != fragmentVoid)
                        addTransition(fragmentVoid);
                    break;
            }
        }
    }

    private String getPrice() {
        String s = "";
        try {
            if (priceMin.getText().toString().length() > 0)
                s += "&priceMin=" + priceMin.getText().toString();
            if (priceMax.getText().toString().length() > 0)
                s += "&priceMax=" + priceMax.getText().toString();
        } catch (NullPointerException e) { /*NOP*/ }
        return s;
    }

    public String getHref() {
        return "http://" + currentCity
                + ".antiagent.ru/index.html?Type=" + type
                + "&Category=" + category
                + fragmentApartment.getRoomsTotal()
                + fragmentRooms.getLivingSpace()
                + fragmentLot.getLotArea()
                + getPrice()
                + "&Zone=" + dataZoneTag.get(spinnerZone.getSelectedItemPosition());
    }

    private void setDefaultDataCity() {
        for (String city : dataCityTag) {
            dataZone.add("Любой район");
            dataZoneTag.add("ALL");
            MainActivity.tinydb.putList("dataZone" + city, getListFromStrings("dataZone" + city));
            MainActivity.tinydb.putList("dataZoneTag" + city, getListFromStrings("dataZoneTag" + city));
        }
    }

    public String getCity() {
        return currentCity;
    }

    public ArrayList<String> getListFromStrings(String key) {
        int resId = getActivity().getResources().getIdentifier(key, "string", getActivity().getPackageName());
        String[] myList = TextUtils.split(getActivity().getResources().getString(resId), "‚‗‚");
        return new ArrayList<>(Arrays.asList(myList));
    }

    private void restartButtons() {
        switch (category) {
            case "APARTMENT":
                setCategory(R.id.buttonAPARTMENT);
                break;
            case "ROOMS":
                setCategory(R.id.buttonROOMS);
                break;
            case "LOT":
                setCategory(R.id.buttonLOT);
                break;
            case "HOUSE":
                setCategory(R.id.buttonHOUSE);
                break;
            case "GARAGE":
                setCategory(R.id.buttonGARAGE);
                break;
            case "COMMERCIAL":
                setCategory(R.id.buttonCOMMERCIAL);
                break;
        }
        switch (type) {
            case "SELL":
                setType(R.id.buttonSELL);
                break;
            case "RENT":
                setType(R.id.buttonRENT);
                break;
        }
    }

}
