package com.presekal.alfan.auth10;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AddActivity extends Activity {

    EditText IDEntry;
    EditText PassCode;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        IDEntry = (EditText) findViewById(R.id.IDEntry);
        PassCode = (EditText) findViewById(R.id.PassCode);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonEnter(View v){

        SharedPreferences sharedPref = getSharedPreferences("SecretData", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor  = sharedPref.edit();


        if(PassCode.getText().toString().equals("pass123")) {
            editor.putString("ID", IDEntry.getText().toString());
            Toast.makeText(this, "Saved "+IDEntry.getText().toString(), Toast.LENGTH_LONG).show();
            editor.apply();
        }
        else{
            String OldValue = sharedPref.getString("ID","");
            editor.putString("ID", OldValue);
            Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show();
        }


    }
}
