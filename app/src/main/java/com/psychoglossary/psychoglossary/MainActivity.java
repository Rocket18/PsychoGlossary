package com.psychoglossary.psychoglossary;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

   Spinner spinner;
    Button confButton, allButton, addButton;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    DatabaseHelper db = new DatabaseHelper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.termSubjectSpinner);
        cursor = db.subquery(DatabaseHelper.SUB_NAME, DatabaseHelper.S_NAME);
        String[] from = new String[]{DatabaseHelper.S_NAME};
        int[] to = { android.R.id.text1};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        spinner.setAdapter(adapter);
        registerForContextMenu(spinner);
        confButton = (Button) findViewById(R.id.psButton);
        allButton = (Button) findViewById(R.id.allButton);
        addButton = (Button) findViewById(R.id.addButton);
        confButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        cursor = db.subquery(DatabaseHelper.SUB_NAME, DatabaseHelper.S_NAME);
        String[] from = new String[]{DatabaseHelper.S_NAME};
        int[] to = { android.R.id.text1};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        spinner.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.psButton:
                try{
                    Intent i = new Intent(this, ViewActivity.class);
                    i.putExtra("subject", cursor.getString(cursor.getColumnIndex(DatabaseHelper.S_NAME)));
                    startActivity(i);
                }
                catch(Throwable e){
                    Toast.makeText(getApplicationContext(), "Не вказано категорію!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.allButton:
                Intent i = new Intent(this, ViewActivity.class);
                i.putExtra("subject", "%%");
                startActivity(i);
                break;
            case R.id.addButton:
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.deleteterm:
                deleteSubject();
                break;
        }
        return super.onContextItemSelected(item);
    }
    void deleteSubject(){
        String sub = cursor.getString(cursor.getColumnIndex(DatabaseHelper.S_NAME));
        db.delete(DatabaseHelper.SUB_NAME, cursor.getLong(cursor.getColumnIndex(DatabaseHelper.S_ID)));
        db.delete(DatabaseHelper.TERM_NAME, sub);
        cursor = db.subquery(DatabaseHelper.SUB_NAME, DatabaseHelper.S_NAME);
        adapter.changeCursor(cursor);
    }
}
