package com.timurkaSoft.AntiAgent.HeadFragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.DialogOneButton;
import com.nispok.snackbar.Snackbar;
import com.timurkaSoft.AntiAgent.HtmlHelper;
import com.timurkaSoft.AntiAgent.MainActivity;
import com.timurkaSoft.AntiAgent.R;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HeadFragmentSettings extends Fragment implements View.OnClickListener {

    EditText updateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.head_fragment_settings, container, false);

        view.findViewById(R.id.tvUpdateZone).setOnClickListener(this);
        view.findViewById(R.id.tvLike).setOnClickListener(this);
        view.findViewById(R.id.tvSendReport).setOnClickListener(this);
        view.findViewById(R.id.tvAbout).setOnClickListener(this);

        updateTime = (EditText) view.findViewById(R.id.tvUpdateTime);
        updateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if ("0".equals(text)) {
                    updateTime.setText("1");
                } else if (text.length() > 0) {
                    try {
                        int minutes = Integer.parseInt(text);
                        updateTime.setHint(text);
                        MainActivity.tinydb.putInt("updateTime", minutes);

                        if (MainActivity.tinydb.getBoolean("updaterIsRun")) {
                            ((MainActivity) getActivity()).startUpdater();
                        }
                    } catch (Exception e) { /* NOP */ }
                }
            }
        });
        int minutes = MainActivity.tinydb.getInt("updateTime");
        if (minutes > 0)
            updateTime.setText(String.valueOf(minutes));
        else
            updateTime.setText("5");

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkboxPhoto);
        checkBox.setChecked(MainActivity.tinydb.getBoolean("no_images"));
        checkBox.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                MainActivity.tinydb.putBoolean("no_images", isChecked);
            }
        });

        return view;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.tvUpdateZone:
                new UpdateZone().execute();
                break;
            case R.id.tvLike:
                rateMe();
                break;
            case R.id.tvSendReport:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:tumolllaa@gmail.com"));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Snackbar.with(getActivity())
                            .text("Необходимо приложение для отправки email")
                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                            .show(getActivity());
                }
                break;
            case R.id.tvAbout:
                DialogOneButton dialogAbout = new DialogOneButton(getActivity(), "О приложении",
                        "Данное приложение является неофициальным мобильным клиентом для сайта www.antiagent.ru" +
                                "\nАвтор приложения не имеет никакого отношения к самому сайту и объявлениям, размещенным на нем." +
                                "\n\nЕсли у вас возникли затруднения при работе с приложением или появились идеи по его развитию, " +
                                "пишите мне на адрес tumolllaa@gmail.com");
                dialogAbout.show();
                break;
        }
    }

    public void rateMe() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    private class UpdateZone extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Обновляю список районов...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(13);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < HeadFragmentSearch.dataCityTag.length; i++) {
                String city = HeadFragmentSearch.dataCityTag[i];
                ArrayList<String> dataZone = new ArrayList<>();
                ArrayList<String> dataZoneTag = new ArrayList<>();
                dataZone.add("Любой район");
                dataZoneTag.add("ALL");
                try {
                    HtmlHelper hh = new HtmlHelper(new URL("https://" + city + ".antiagent.ru/ajax.php?func=geoselector"));
                    List<TagNode> links = hh.getLinksByClass("b-link b-geo-link");
                    for (int j = 0; j < links.size(); j++) {
                        dataZone.add(links.get(j).getText().toString());
                        dataZoneTag.add(getTag(links.get(j).getAttributes().toString()));
                    }
                    if (links.size() > 0) {
                        MainActivity.tinydb.putList("dataZone" + city, dataZone);
                        MainActivity.tinydb.putList("dataZoneTag" + city, dataZoneTag);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                publishProgress(i + 1);
            }
            return null;
        }

        private String getTag(String s) {
            String[] split = s.split(", ");
            return split[1].substring(3);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

}
