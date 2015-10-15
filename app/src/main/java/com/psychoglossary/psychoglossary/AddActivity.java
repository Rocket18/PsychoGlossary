package com.psychoglossary.psychoglossary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Rocke on 10/11/2015.
 */
public class AddActivity extends Activity implements View.OnClickListener {
    DatabaseHelper dh = new DatabaseHelper(this);
    Button confirmButton, addSubButton;
    EditText name, desc, sub;
    Spinner spinner;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        addSubButton = (Button) findViewById(R.id.addSubButton);
        confirmButton.setOnClickListener(this);
        addSubButton.setOnClickListener(this);
        name = (EditText) findViewById(R.id.termName);
        desc = (EditText) findViewById(R.id.termDescription);
        sub = (EditText) findViewById(R.id.subjectName);
        spinner = (Spinner) findViewById(R.id.subjectsSpinner);
        cursor = dh.subquery(DatabaseHelper.SUB_NAME, DatabaseHelper.S_NAME);
        String[] from = new String[]{DatabaseHelper.S_NAME};
        int[] to = { android.R.id.text1};
        try{
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
            spinner.setAdapter(adapter);
        }
        catch (Exception e){
            adapter = null;
        }
    }

    void addTerm(String term, String decsription, String subject) throws DatabaseHelper.DatabaseException {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TERM, term);
        values.put(DatabaseHelper.DESC, decsription);
        values.put(DatabaseHelper.SUB, subject);
        dh.insert(DatabaseHelper.TERM_NAME, values);
        Toast.makeText(getApplicationContext(), "Успішно додано!", Toast.LENGTH_SHORT).show();
    }
    void addSubject(String subject) throws DatabaseHelper.DatabaseException {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.S_NAME, subject);
        dh.insert(DatabaseHelper.SUB_NAME, values);
        Toast.makeText(getApplicationContext(), "Успішно додано!", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("NewApi") @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                try {
                    addTerm(name.getText().toString(), desc.getText().toString(), cursor.getString(cursor.getColumnIndex(DatabaseHelper.S_NAME)));
                    name.getText().clear();
                    desc.getText().clear();
                    name.requestFocus();
                } catch (DatabaseHelper.DatabaseException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                catch(Throwable e){
                    Toast.makeText(getApplicationContext(), "Не вказано категорію!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.addSubButton:
                if(!sub.isEnabled()){
                    sub.setEnabled(true);
                    sub.setVisibility(View.VISIBLE);
                    sub.requestFocus();
                    addSubButton.setText(R.string._addsub);
                }
                if(sub.isEnabled() && !sub.getText().toString().isEmpty()){
                    try {
                        addSubject(sub.getText().toString());
                        sub.getText().clear();
                    } catch (DatabaseHelper.DatabaseException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    cursor = dh.subquery(DatabaseHelper.SUB_NAME, DatabaseHelper.S_NAME);
                    adapter.changeCursor(cursor);
                }
                break;
        }
    }
}
