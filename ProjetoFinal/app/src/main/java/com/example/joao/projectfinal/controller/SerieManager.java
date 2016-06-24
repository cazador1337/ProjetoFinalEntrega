package com.example.joao.projectfinal.controller;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.example.joao.projectfinal.R;
import com.example.joao.projectfinal.banco.sql.Q;
import com.example.joao.projectfinal.custom.SeriesAdapter;
import com.example.joao.projectfinal.models.Serie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by joao on 23/06/2016.
 */
public class SerieManager {
    private ListView listView;
    private boolean onlyWatching = false;
    private boolean onlyComplete = false;
    private boolean onlyPlanToWatch = false;
    private SeriesAdapter adapter;
    private List<Serie> list;

    public SerieManager(ListView listView, Activity main) {
        this.listView = listView;
        list = new LinkedList<>();
        adapter = new SeriesAdapter(main, R.layout.list_series, list);
        listView.setAdapter(adapter);
    }

    public void update() {
        if (onlyComplete || onlyPlanToWatch || onlyWatching) {
            setList(Serie.objects.filter(getFilter()));
        } else {
            setList(Serie.objects.all());
        }
    }

    public void setOnlyWatching(boolean onlyWatching) {
        this.onlyWatching = onlyWatching;
    }

    public void setOnlyComplete(boolean onlyComplete) {
        this.onlyComplete = onlyComplete;
    }

    public void setOnlyPlanToWatch(boolean onlyPlanToWatch) {
        this.onlyPlanToWatch = onlyPlanToWatch;
    }

    public void search(String query) {
        setList(Serie.objects.filter(getFilter(Q.like(Serie.TITULO, query), Q.OR, Q.like(Serie.GENEROS, query))));
    }

    private Q.Option[] getFilter() {
        int size = 0;
        boolean[] bols = new boolean[]{onlyComplete, onlyWatching, onlyPlanToWatch};
        Q.Option[] opts = new Q.Option[]{Q.equal(Serie.STATUS, Serie.COMPLETO),
                Q.equal(Serie.STATUS, Serie.ASSISTINDO), Q.equal(Serie.STATUS, Serie.PLANO)};
        ArrayList<Q.Option> array = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (bols[i]) {
                array.add(opts[i]);
                array.add(Q.OR);
            }
        }
        if (array.isEmpty()) return null;
        array.remove(array.size() - 1);
        return array.toArray(new Q.Option[array.size()]);
    }

    private Q.Option[] getFilter(Q.Option... more) {
        ArrayList<Q.Option> array = new ArrayList<>();
        Q.Option[] aux = getFilter();
        if (aux != null) {
            for (Q.Option o : aux) {
                array.add(o);
            }
            array.add(Q.AND);
        }

        for (Q.Option o : more) {
            array.add(o);
        }
        return array.toArray(new Q.Option[array.size()]);
    }

    public void setList(Serie[] series) {
        list.clear();
        if (series != null) {
            for (Serie s : series) {
                list.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public Serie get(int position) {
        return list.get(position);
    }
}
