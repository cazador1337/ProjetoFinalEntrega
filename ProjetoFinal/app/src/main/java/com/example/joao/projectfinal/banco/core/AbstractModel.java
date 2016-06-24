package com.example.joao.projectfinal.banco.core;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.example.joao.projectfinal.banco.annotations.Attribute;
import com.example.joao.projectfinal.banco.annotations.PrimaryKey;
import com.example.joao.projectfinal.banco.sql.Q;
import com.example.joao.projectfinal.banco.sql.SQLUtils;
import com.example.joao.projectfinal.banco.sql.Settings;

/**
 * Created by joao on 11/06/2016.
 */
public abstract class AbstractModel<T extends Parcelable> {
    private Class<T> model;
    protected static Objects<?> objects = new Objects<>();
    public static Manager manager;
    public static Settings settings;

    public static void open(Context context, String database, int version, Class<?> type) {
        Settings settings = new Settings(database, version, type);
        settings.setContext(context);
        AbstractModel.settings = settings;
        AbstractModel.manager = new Manager(settings);
    }

    public AbstractModel(Class<T> model) {
        this.model = model;
    }

    public AbstractModel() {

    }

    private final ContentValues setValues(Field[] fields) throws IllegalAccessException {
        ContentValues cv = new ContentValues();
        for (Field f : fields) {
            f.setAccessible(true);
            Attribute a = f.getAnnotation(Attribute.class);
            PrimaryKey p = f.getAnnotation(PrimaryKey.class);
            if (a != null && p == null) {
                if (a.type() == Integer.class || a.type() == int.class) {
                    cv.put(a.column(), Integer.parseInt(f.get(this).toString()));
                } else if (a.type() == String.class) {
                    cv.put(a.column(), f.get(this).toString());
                } else if (a.type() == Float.class || a.type() == float.class) {
                    cv.put(a.column(), Float.parseFloat(f.get(this).toString()));
                } else if (a.type() == long.class) {
                    cv.put(a.column(), Long.parseLong(f.get(this).toString()));
                }
            }
        }
        return cv;
    }

    private final Field findKey(Field[] fields) {
        Field keyF = null;
        for (Field f : fields) {
            f.setAccessible(true);
            PrimaryKey p = f.getAnnotation(PrimaryKey.class);
            if (p != null) {
                return f;
            }
        }
        return null;
    }

    private final void setKey(long id) throws IllegalAccessException {
        Field key = findKey(model.getDeclaredFields());
        if (key != null) {
            key.set(this, (long) id);
        }
    }

    public final void save() {
        try {
            SQLiteDatabase db = AbstractModel.manager.getWritableDatabase();
            ContentValues cv = setValues(model.getDeclaredFields());
            long id = db.insert(SQLUtils.findTable(model), null, cv);
            setKey(id);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final int del() {
        try {
            SQLiteDatabase db = manager.getWritableDatabase();
            Field keyF = findKey(model.getDeclaredFields());
            Attribute key = keyF.getAnnotation(Attribute.class);
            int r = db.delete(SQLUtils.findTable(model), Q.equal(key.column()), new String[]{keyF.get(this).toString()});
            db.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public final int update() {
        try {
            SQLiteDatabase db = manager.getWritableDatabase();
            ContentValues cv = setValues(model.getDeclaredFields());
            Field keyF = findKey(model.getDeclaredFields());
            Attribute key = keyF.getAnnotation(Attribute.class);
            String[] dic = new String[]{keyF.get(this).toString()};
            int r = db.update(SQLUtils.findTable(model), cv, Q.equal(key.column()), dic);
            db.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class Objects<T extends Parcelable> {
        public static String SQL;

        Objects() {
        }

        private String[] convert(Object... list) {
            String[] r = new String[list.length];
            int i = 0;
            for (Object o : list) {
                r[i++] = o.toString();
            }
            return r;
        }

        private T setData(Cursor cs, Field f, T row) throws IllegalAccessException {
            f.setAccessible(true);
            Attribute attribute = f.getAnnotation(Attribute.class);
            if (attribute != null) {
                if (attribute.type() == String.class) {
                    f.set(row, cs.getString(cs.getColumnIndex(attribute.column())));
                } else if (attribute.type() == int.class || attribute.type() == Integer.class) {
                    f.set(row, cs.getInt(cs.getColumnIndex(attribute.column())));
                } else if (attribute.type() == Float.class || attribute.type() == float.class) {
                    f.set(row, cs.getFloat(cs.getColumnIndex(attribute.column())));
                } else if (attribute.type() == long.class) {
                    f.set(row, cs.getLong(cs.getColumnIndex(attribute.column())));
                }
            }
            return row;
        }

        private T.Creator<T> getCreator(Class<?> core) throws NoSuchFieldException, IllegalAccessException {
            Field field = core.getField("CREATOR");
            field.setAccessible(true);
            return (T.Creator) field.get(null);
        }

        public T[] all() {
            SQLiteDatabase db = AbstractModel.manager.getReadableDatabase();
            try {
                Class<?> core = AbstractModel.settings.getCore();
                Cursor cs = db.rawQuery(SQLUtils.makeSelect(core), null);
                T[] array = getCreator(core).newArray(cs.getCount());
                Field[] fields = core.getDeclaredFields();
                int p = 0;
                while (cs.moveToNext()) {
                    T row = (T) core.newInstance();
                    for (Field f : fields) {
                        setData(cs, f, row);
                    }
                    array[p++] = row;
                }
                return array;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public T get(Q.Option... args) {
            SQLiteDatabase db = AbstractModel.manager.getReadableDatabase();
            StringBuilder where = new StringBuilder(" where ");
            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                where.append(args[i].getExpression());
                if (args[i].getValue() != null) {
                    values.add(args[i].getValue());
                }
            }

            try {
                Class<?> core = AbstractModel.settings.getCore();
                SQL = SQLUtils.makeSelect(core) + where.toString();
                Cursor cs = db.rawQuery(SQL, convert(values.toArray()));
                Field[] fields = core.getDeclaredFields();
                if (cs.moveToNext()) {
                    T row = (T) core.newInstance();
                    for (Field f : fields) {
                        setData(cs, f, row);
                    }
                    return row;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public T[] filter(Q.Option... args) {
            SQLiteDatabase db = AbstractModel.manager.getReadableDatabase();
            StringBuilder where = new StringBuilder(" where ");
            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                where.append(args[i].getExpression());
                if (args[i].getValue() != null) {
                    values.add(args[i].getValue());
                }
            }

            try {
                Class<?> core = AbstractModel.settings.getCore();
                SQL = SQLUtils.makeSelect(core) + where.toString();
                Cursor cs = db.rawQuery(SQL, convert(values.toArray()));

                Field field = core.getField("CREATOR");
                field.setAccessible(true);
                T.Creator<T> creator = (T.Creator) field.get(null);
                T[] array = creator.newArray(cs.getCount());
                Field[] fields = core.getDeclaredFields();

                int p = 0;
                while (cs.moveToNext()) {
                    T row = (T) core.newInstance();
                    for (Field f : fields) {
                        setData(cs, f, row);
                    }
                    array[p++] = row;
                }
                return array;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public Object[] actions(Q.Action... args) {
            SQLiteDatabase db = AbstractModel.manager.getReadableDatabase();
            StringBuilder selection = new StringBuilder("");
            for (int i = 0; i < args.length; i++) {
                selection.append(args[i].getExpression().replace("?", args[i].getCollumn()) + ", ");
            }
            String functions = selection.substring(0, selection.length() - 2);

            try {
                Class<?> core = AbstractModel.settings.getCore();
                SQL = SQLUtils.makeSelect(core).replace("*", functions);
                Cursor cs = db.rawQuery(SQL, null);
                Object[] data = new Object[cs.getColumnCount()];
                if (cs.moveToNext()) {
                    for (int i = 0; i < data.length; i++) {
                        if(args[i].getType() == float.class){
                            data[i] = cs.getFloat(i);
                        }else if(args[i].getType() == int.class){
                            data[i] = cs.getInt(i);
                        }
                    }
                }
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public int drop() {
            try {
                SQLiteDatabase db = manager.getWritableDatabase();
                int r = db.delete(SQLUtils.findTable(settings.getCore()), null, null);
                db.close();
                return r;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
