package com.timurkaSoft.AntiAgent.HeadFragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gc.materialdesign.widgets.Dialog;
import com.timurkaSoft.AntiAgent.MainActivity;
import com.timurkaSoft.AntiAgent.R;
import com.timurkaSoft.AntiAgent.ShortInfo;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class HeadFragmentFavorites extends HeadFragmentAdvert {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.head_fragment_advert, container, false);

        mapArrayList.clear();
        elements.clear();
        readDB();
        createList(view);

        return view;
    }

    @Override
    public void refresh() {
        mapArrayList.clear();
        elements.clear();
        readDB();
        adapter.notifyDataSetChanged();
    }

    private void readDB() {
        Cursor cursor = cupboard().withDatabase(MainActivity.db).query(ShortInfo.class).getCursor();
        QueryResultIterable<ShortInfo> itr = cupboard().withCursor(cursor).iterate(ShortInfo.class);
        for (ShortInfo element : itr) {
            if (element == null) break;
            elements.add(element);
            mapArrayList.add(element.getMap());
        }
        itr.close();
        cursor.close();
    }

    protected void createList(View view) {
        adapter = new SimpleAdapter(getActivity(), mapArrayList, R.layout.ad_list_item,
                new String[]{"text", "price", "photo"}, new int[]{R.id.text, R.id.price, R.id.photoCount});
        ListView listViewData = (ListView) view.findViewById(R.id.listViewData);
        listViewData.setAdapter(adapter);
        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click(position);
            }
        });
        listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                longClick(i);
                return true;
            }
        });
    }

    private void longClick(final int position) {
        Dialog dialog = new Dialog(getActivity(), "Удалить объявление из избранного?");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cupboard().withDatabase(MainActivity.db).delete(ShortInfo.class, "href = ?", elements.get(position).getHref());
                refresh();
                ((MainActivity) getActivity()).voidFavorites();
            }
        });
        dialog.show();
    }

}
