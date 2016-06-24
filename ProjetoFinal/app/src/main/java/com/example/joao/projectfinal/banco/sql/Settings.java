package com.example.joao.projectfinal.banco.sql;


import android.content.Context;

/**
 * Created by joao on 11/06/2016.
 */
public class Settings {

    private String database;
    private int version;
    private Class<?> core;
    private Context context;

    public Settings() {
    }

    public Settings(String database, int version, Class<?> core) {
        this.database = database;
        this.version = version;
        this.core = core;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Class<?> getCore() {
        return core;
    }

    public void setCore(Class<?> core) {
        this.core = core;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
