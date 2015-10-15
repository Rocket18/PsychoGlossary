package com.psychoglossary.psychoglossary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Rocke on 10/11/2015.
 */
public class UpdateActivity extends AddActivity {
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        confirmButton.setText("Оновити");
        i = getIntent();
        name.setText(i.getStringExtra("term"));
        desc.setText(i.getStringExtra("desc"));
        final long id = i.getLongExtra("id", 0);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                i = new Intent(getBaseContext(), ViewActivity.class);
                i.putExtra("retId", id);
                i.putExtra("retName", name.getText().toString());
                i.putExtra("retDesc", desc.getText().toString());
                i.putExtra("retSub", cursor.getString(cursor.getColumnIndex(DatabaseHelper.S_NAME)));
                startActivity(i);
                finish();
            }
        });
    }
}
