package com.example.joao.projectfinal.banco.core;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.joao.projectfinal.banco.sql.SQLUtils;
import com.example.joao.projectfinal.banco.sql.Settings;


/**
 * Created by joao on 11/06/2016.
 */
public class Manager extends SQLiteOpenHelper {
    private Settings settings;

    public Manager(Settings settings) {
        super(settings.getContext(), settings.getDatabase(), null, settings.getVersion());
        this.settings = settings;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQLUtils.makeCreate(settings.getCore()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //not implemented
    }

    public void dropDatabase() {
        settings.getContext().deleteDatabase(settings.getDatabase());
    }
}
