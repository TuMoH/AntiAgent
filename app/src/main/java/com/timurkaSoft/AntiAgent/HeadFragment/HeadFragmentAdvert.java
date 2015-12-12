package com.timurkaSoft.AntiAgent.HeadFragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.timurkaSoft.AntiAgent.MainActivity;
import com.timurkaSoft.AntiAgent.R;
import com.timurkaSoft.AntiAgent.ShortInfo;
import com.timurkaSoft.AntiAgent.SiteParser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadFragmentAdvert extends Fragment {

    List<ShortInfo> elements = new ArrayList<>();
    String currentHref;
    SimpleAdapter adapter;
    View footerView;
    TextView footerTextView;
    TextView tvError;
    ProgressWheel footerProgressBar;
    ListView listViewData;
    Boolean hasProgress = false;
    ArrayList<HashMap<String, String>> mapArrayList = new ArrayList<>();
    int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.head_fragment_advert, container, false);

        footerView = getActivity().getLayoutInflater().inflate(R.layout.footer, null);
        footerTextView = (TextView) footerView.findViewById(R.id.footerTextView);
        footerProgressBar = (ProgressWheel) footerView.findViewById(R.id.footerProgressBar);

        tvError = (TextView) view.findViewById(R.id.tvError);

        createList(view);

        currentHref = MainActivity.advertHref;
        mapArrayList.clear();
        elements.clear();
        page = 1;
        new ListCreator(view).execute(currentHref);

        return view;
    }

    protected void click(int position) {
        if (position > elements.size()) {
            Snackbar.with(getActivity()).text("Произошла ошибка, обновите список").show(getActivity());
        }
        if (position == mapArrayList.size()) {
            if (!hasProgress) {
                page++;
                new ListCreator(getView()).execute(currentHref + "&page=" + page);
            }
        } else {
            MainActivity.shortInfo = elements.get(position);
            ((MainActivity) getActivity()).transactionToInfo();
        }
    }

    public void refresh() {
        mapArrayList.clear();
        elements.clear();
        adapter.notifyDataSetChanged();
        page = 1;
        new ListCreator(getView()).execute(currentHref);
    }

    protected void createList(View view) {
        adapter = new SimpleAdapter(getActivity(), mapArrayList, R.layout.ad_list_item,
                new String[]{"text", "price", "photo"}, new int[]{R.id.text, R.id.price, R.id.photoCount});
        listViewData = (ListView) view.findViewById(R.id.listViewData);
        listViewData.addFooterView(footerView);
        listViewData.setAdapter(adapter);
        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click(position);
            }
        });
    }

    private class ListCreator extends AsyncTask<String, Void, Void> {

        View view;
        String error;

        private ListCreator(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            error = "К сожалению, по вашему запросу объявлений не найдено.\n" +
                    "При подаче объявления не всегда указываются все параметры, попробуйте задать меньше условий поиска.";
            hasProgress = true;
            footerTextView.setText("Ищу объявления");
            footerProgressBar.setVisibility(View.VISIBLE);
            footerTextView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
        }

        protected Void doInBackground(String... arg) {
            try {
                if (MainActivity.fromUpdater) {
                    List<Parcelable> parcelables = getActivity().getIntent().getParcelableArrayListExtra("news");
                    for (Parcelable parcelable : parcelables) {
                        ShortInfo element = Parcels.unwrap(parcelable);
                        elements.add(element);
                        mapArrayList.add(element.getMap());
                    }
                    MainActivity.fromUpdater = false;
                } else {
                    for (ShortInfo element : SiteParser.getShortInfoList(arg[0])) {
                        elements.add(element);
                        mapArrayList.add(element.getMap());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                error = "Не удалось загрузить объявления. Ошибка соединения.";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            footerTextView.setText("Найти еще");
            footerProgressBar.setVisibility(View.INVISIBLE);
            if (mapArrayList.size() == 0) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
                footerTextView.setVisibility(View.INVISIBLE);
            }
            hasProgress = false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hasProgress = false;
        }
    }

}
