package com.example.joao.projectfinal.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.example.joao.projectfinal.banco.annotations.Attribute;
import com.example.joao.projectfinal.banco.annotations.CharField;
import com.example.joao.projectfinal.banco.annotations.PrimaryKey;
import com.example.joao.projectfinal.banco.annotations.Table;
import com.example.joao.projectfinal.banco.core.AbstractModel;
import com.example.joao.projectfinal.custom.SeriesAdapter;

/**
 * Created by joao on 23/06/2016.
 */
public class Serie extends AbstractModel<Serie> implements Parcelable, View.OnClickListener {
    public static final int ASSISTINDO = 0, PLANO = 1, COMPLETO = 2;
    public static final String ID = "ID", TOTAL_EPS = "TOTAL_EPS", TITULO = "TITULO",
    GENEROS = "GENEROS", NOTA = "NOTA", STATUS = "STATUS", EPS_VISTOS = "EPS_VISTOS",
    DATA_BASE = "BD2";
    public static final Serie.Objects<Serie> objects = (Objects<Serie>) AbstractModel.objects;

    @Table(name = "seires")
    public Serie(){
        super(Serie.class);
    }

    @Attribute(column = ID, type = long.class)
    @PrimaryKey(auto_generated = true)
    private long id = -1;

    @Attribute(column = TOTAL_EPS, type = int.class)
    private int eps;

    @Attribute(column = TITULO, type = String.class)
    @CharField(max_length = 100)
    private String titulo;

    @Attribute(column = GENEROS, type = String.class)
    @CharField(max_length = 300)
    private String generos;

    @Attribute(column = NOTA, type = float.class)
    private float nota;

    @Attribute(column = STATUS, type = int.class)
    private int status;

    @Attribute(column = EPS_VISTOS, type = int.class)
    private int eps_vistos;

    private SeriesAdapter updater;

    protected Serie(Parcel in) {
        super(Serie.class);
        id = in.readLong();
        eps = in.readInt();
        titulo = in.readString();
        generos = in.readString();
        nota = in.readFloat();
        status = in.readInt();
        eps_vistos = in.readInt();
    }

    public static final Creator<Serie> CREATOR = new Creator<Serie>() {
        @Override
        public Serie createFromParcel(Parcel in) {
            return new Serie(in);
        }

        @Override
        public Serie[] newArray(int size) {
            return new Serie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(eps);
        dest.writeString(titulo);
        dest.writeString(generos);
        dest.writeFloat(nota);
        dest.writeInt(status);
        dest.writeInt(eps_vistos);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getEps() {
        return eps;
    }

    public void setEps(int eps) {
        this.eps = eps;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGeneros() {
        return generos;
    }

    public void setGeneros(String generos) {
        this.generos = generos;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEps_vistos() {
        return eps_vistos;
    }

    public void setEps_vistos(int eps_vistos) {
        this.eps_vistos = eps_vistos;
    }

    public SeriesAdapter getUpdater() {
        return updater;
    }

    public void setUpdater(SeriesAdapter updater) {
        this.updater = updater;
    }

    @Override
    public void onClick(View v) {
        if(eps_vistos < eps){
            eps_vistos++;
            this.update();
            updater.notifyDataSetChanged();
        }
    }
}
