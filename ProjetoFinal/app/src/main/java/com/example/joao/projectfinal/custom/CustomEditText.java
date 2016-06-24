package com.example.joao.projectfinal.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AutoCompleteTextView;

/**
 * Created by joao on 20/06/2016.
 */
public class CustomEditText extends AutoCompleteTextView {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if(text.toString().contains(",")){
            text = text.subSequence(text.toString().lastIndexOf(",")+1, text.length()).toString().trim();
        }
        super.performFiltering(text, keyCode);
    }

    @Override
    protected void replaceText(CharSequence text) {
        if(this.getText().toString().contains(",")){
            String aux = this.getText().toString();
            aux = aux.substring(0, aux.lastIndexOf(",")+1)+" ";
            super.replaceText(text);
            super.setText(aux+super.getText().toString());
            super.setSelection(super.getText().length());
        }else{
            super.replaceText(text);
        }

    }
}
