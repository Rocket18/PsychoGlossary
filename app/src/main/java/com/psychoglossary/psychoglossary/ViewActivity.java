package com.psychoglossary.psychoglossary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Rocke on 10/11/2015.
 */
public class ViewActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView listview;
    EditText searchText;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    AlertDialog.Builder ab;
    String subject;
    DatabaseHelper dh = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra("retName")) {
            Intent i = getIntent();
            subject = i.getStringExtra("retSub");
            try {
                updateTerm(i.getLongExtra("retId", 0), i.getStringExtra("retName"), i.getStringExtra("retDesc"), subject);
            } catch (DatabaseHelper.DatabaseException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else subject = getIntent().getStringExtra("subject");
        setContentView(R.layout.activity_view);
        super.onCreate(savedInstanceState);
        ab = new AlertDialog.Builder(ViewActivity.this);
        searchText = (EditText) findViewById(R.id.searchText);
        listview = (ListView) findViewById(R.id.listView1);
        listview.setOnItemClickListener(this);
        registerForContextMenu(listview);
        cursor = dh.query(DatabaseHelper.TERM_NAME, subject,  DatabaseHelper.TERM);
        String[] from = new String[]{DatabaseHelper.TERM};
        int[] to = { android.R.id.text1};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        listview.setAdapter(adapter);
        if(cursor.isAfterLast())
            Toast.makeText(getApplicationContext(), "Немає жодного терміну", Toast.LENGTH_SHORT).show();
        searchTerm();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_main, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.deleteterm:
                deleteTerm();
                break;
            case R.id.updateterm:
                Intent i = new Intent(this, UpdateActivity.class);
                i.putExtra("id", cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID)));
                i.putExtra("term", cursor.getString(cursor.getColumnIndex(DatabaseHelper.TERM)));
                i.putExtra("desc", cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESC)));
                startActivity(i);
                finish();
                break;
        }
        return super.onContextItemSelected(item);
    }
    void getDescription(){
        ab.setMessage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESC))).setCancelable(true).setNeutralButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
    @SuppressLint("NewApi")
    void searchTerm(){
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!searchText.getText().toString().isEmpty())
                    cursor = dh.search(DatabaseHelper.TERM_NAME, DatabaseHelper.TERM, s.toString(), subject, DatabaseHelper.TERM);
                else
                    cursor = dh.query(DatabaseHelper.TERM_NAME, subject,  DatabaseHelper.TERM);
                adapter.changeCursor(cursor);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    void deleteTerm(){
        dh.delete(DatabaseHelper.TERM_NAME, cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID)));
        cursor = dh.query(DatabaseHelper.TERM_NAME, subject, DatabaseHelper.TERM);
        adapter.changeCursor(cursor);
    }
    void updateTerm(long id, String newName, String newDesc, String sub) throws DatabaseHelper.DatabaseException {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TERM, newName);
        values.put(DatabaseHelper.DESC, newDesc);
        values.put(DatabaseHelper.SUB, sub);
        dh.update(DatabaseHelper.TERM_NAME, id, values);
    }
    @Override
    public void onItemClick(AdapterView<?> av, View v, int i, long l) {
        getDescription();
    }
}
