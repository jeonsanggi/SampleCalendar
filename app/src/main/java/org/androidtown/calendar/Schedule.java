package org.androidtown.calendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Jeon on 2017-06-01.
 */

public class Schedule extends AppCompatActivity{

    DatePicker datePicker;
    EditText titleText;
    EditText editText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        titleText = (EditText) findViewById(R.id.titleText);
        editText = (EditText) findViewById(R.id.editText);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                //String msg = String.format("%d / %d / %d", year,monthOfYear+1, dayOfMonth);
                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu write_menu){
        getMenuInflater().inflate(R.menu.write_menu, write_menu);
        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if( id == R.id.done ) {
            String strTitle = titleText.getText().toString();
            String strContent = editText.getText().toString();
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();

            Intent intent = getIntent();
            intent.putExtra("title", strTitle);
            intent.putExtra("content", strContent);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", day);

            setResult(1, intent);
            finish();
            return true;
        }
        else if( id == R.id.cancel) {
            Intent intent = getIntent();
            setResult(0, intent);
            finish();
            return true;
        }
        else if( id == R.id.home ) {
            Intent intent = getIntent();
            setResult(0, intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}