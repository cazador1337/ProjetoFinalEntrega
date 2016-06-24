package com.example.joao.projectfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.example.joao.projectfinal.models.Serie;

public class ModifyActivity extends AppCompatActivity {
    private Serie s = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        initComponents();
        s = getIntent().getParcelableExtra(Serie.ID);
        s = s == null ? new Serie() : s;
        if(s.getId() != -1){
            setOnEditText(s.getTitulo(), R.id.form_title);
            setOnEditText(s.getEps()+"",R.id.form_eps);
            setOnEditText(s.getGeneros(), R.id.form_genres);
            RatingBar bar = (RatingBar) findViewById(R.id.form_nota);
            bar.setRating(s.getNota());
            setStatus(s.getStatus());
        }
    }

    private void initComponents() {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.form_genres);
        String[] genres = getResources().getStringArray(R.array.genres);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genres);
        textView.setAdapter(adapter);
    }

    private void setOnEditText(String value, int id){
        EditText editText = (EditText) findViewById(id);
        editText.setText(value);
    }

    public void save(View v){
        s.setStatus(getStatus(R.id.form_rd_status));
        s.setGeneros(getFromEdit(R.id.form_genres));
        s.setNota(getFromRating(R.id.form_nota));
        s.setTitulo(getFromEdit(R.id.form_title));
        if(!isEmpty(s.getGeneros(),s.getTitulo(), getFromEdit(R.id.form_eps))){
            s.setEps(Integer.parseInt(getFromEdit(R.id.form_eps)));
            if(s.getId()!=-1){
                s.update();
            }else{
                s.save();
            }
            setResult(RESULT_OK);
            finish();
        }else{
            showAlert(R.id.form_genres, R.id.form_title, R.id.form_eps);
        }
    }

    private void showAlert(int... ids){
        for (int id: ids){
            EditText e = getEdit(id);
            if(isEmpty(e.getText().toString())){
                e.setError("Campo Vazio");
            }
        }
    }

    private EditText getEdit(int id){
        return (EditText) findViewById(id);
    }

    private boolean isEmpty (String... args){
        boolean b = false;
        for(String arg : args){
            b = b || arg.isEmpty();
            if(b){
                return b;
            }
        }
        return b;
    }

    private String getFromEdit(int id){
        EditText e = (EditText) findViewById(id);
        return e.getText().toString();
    }

    private float getFromRating(int id){
        RatingBar rb = (RatingBar) findViewById(id);
        return rb.getRating();
    }

    private int getStatus(int id){
        RadioGroup rg = (RadioGroup) findViewById(id);
        switch (rg.getCheckedRadioButtonId()){
            case R.id.rd_complete:{
                return Serie.COMPLETO;
            }
            case R.id.rd_watching:{
                return Serie.ASSISTINDO;
            }
            case R.id.rd_plan:{
                return Serie.PLANO;
            }
        }
        return -1;
    }

    private void setStatus(int pos){
        switch (pos){
            case Serie.ASSISTINDO:{
                setRadio(R.id.rd_watching, true);
                break;
            }
            case Serie.COMPLETO:{
                setRadio(R.id.rd_complete, true);
                break;
            }
            case Serie.PLANO:{
                setRadio(R.id.rd_plan, true);
                break;
            }
        }
    }

    private void setRadio(int id, boolean b){
        RadioButton r = (RadioButton) findViewById(id);
        r.setChecked(b);
    }

    public void del(View v){
        s.del();
        setResult(RESULT_OK);
        finish();
    }

    public void cancel(View v){
        setResult(RESULT_CANCELED);
        finish();
    }

}
