package com.example.joao.projectfinal.custom;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joao.projectfinal.R;
import com.example.joao.projectfinal.models.Serie;

import java.util.List;

/**
 * Created by joao on 21/06/2016.
 */
public class SeriesAdapter extends ArrayAdapter<Serie> {

    public SeriesAdapter(Context context, int resource, List<Serie> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        Serie data = this.getItem(position);
        if (row == null) {
            LayoutInflater inflater = ((Activity)super.getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.list_series, parent, false);
        }
        setOnTextView(data.getTitulo(), R.id.list_title, row);
        setOnTextView(data.getEps_vistos() + "", R.id.list_eps_vistos, row);
        setOnTextView(data.getEps() + "/", R.id.list_total_eps, row);
        setOnTextView("Nota: " + data.getNota(), R.id.list_nota, row);

        ImageButton button = (ImageButton) row.findViewById(R.id.add_ep);
        button.setOnClickListener(data);
        data.setUpdater(this);
        return row;
    }

    private void setOnTextView(String value, int id, View row){
        TextView tv = (TextView) row.findViewById(id);
        tv.setText(value);
    }
}
