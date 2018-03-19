package org.androidtown.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 그리드뷰를 이용해 월별 캘린더를 만드는 방법에 대해 알 수 있습니다.
 *
 * @author Mike
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 월별 캘린더 뷰 객체
     */
    GridView monthView;

    /**
     * 월별 캘린더 어댑터
     */
    MonthAdapter monthViewAdapter;

    /**
     * 월을 표시하는 텍스트뷰
     */
    TextView monthText;

    /**
     * 일정을 표시하는 리스트뷰
     */
    ListView scheduleList;
    ArrayAdapter<String> scheduleListAdapter;
    View scheduleContentView = null;
    int nScheduleContentIndex = -1;
    Button scheduleRemoveButton;

    /**
     * 현재 연도
     */
    int curYear;

    /**
     * 현재 월
     */
    int curMonth;

    /**
     * SQLite Database
     */
    SQLiteDatabase db = null;
    public int DB_MODE = Context.MODE_PRIVATE;
    public String DB_NAME = "schedule.db";
    public String TABLE_NAME = "schedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 그림자 및 로고
        getSupportActionBar().setElevation(20);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);

        // 데이터베이스 열기 및 테이블 생성
        db = openOrCreateDatabase(DB_NAME, DB_MODE, null);
        //removeDatabaseTable();
        createDatabaseTable();

        // 월별 캘린더 뷰 객체 참조
        monthView = (GridView) findViewById(R.id.monthView);
        monthViewAdapter = new MonthAdapter(this);
        monthView.setAdapter(monthViewAdapter);

        // 리스너 설정
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 현재 선택한 일자 정보 표시
                MonthItem curItem = (MonthItem) monthViewAdapter.getItem(position);
                int day = curItem.getDay();

                Log.d("MainActivity", "Selected : " + day);
            }
        });

        monthText = (TextView) findViewById(R.id.monthText);
        setMonthText();

        scheduleRemoveButton = (Button) findViewById(R.id.removeSchedule);
        scheduleRemoveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (nScheduleContentIndex >= 0) {
                    deleteDatabaseRowByPosition(nScheduleContentIndex);
                    nScheduleContentIndex = -1;

                    updateCalendarSchedule();
                }
            }
        });
        scheduleRemoveButton.setEnabled(false);

        scheduleList = (ListView) findViewById(R.id.listViewSchedule);
        updateCalendarSchedule();
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                if (scheduleContentView != null)
                    scheduleContentView.setBackgroundColor(Color.rgb(0xD3, 0xD3, 0xD3));

                nScheduleContentIndex = position;
                scheduleContentView = arg1;
                arg1.setBackgroundColor(Color.CYAN);

                if (nScheduleContentIndex >= 0) {
                    scheduleRemoveButton.setEnabled(true);
                }
                else {
                    scheduleRemoveButton.setEnabled(false);
                }
            }
        });

        // 이전 월로 넘어가는 이벤트 처리
        Button monthPrevious = (Button) findViewById(R.id.monthPrevious);
        monthPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setPreviousMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });

        // 다음 월로 넘어가는 이벤트 처리
        Button monthNext = (Button) findViewById(R.id.monthNext);
        monthNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setNextMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        if (db != null) {
            db.close();
        }

        super.onDestroy();
    }

    /**
     * 월 표시 텍스트 설정
     */
    private void setMonthText() {
        curYear = monthViewAdapter.getCurYear();
        curMonth = monthViewAdapter.getCurMonth();

        monthText.setText(curYear + "년 " + (curMonth + 1) + "월");
    }

    private void createDatabaseTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(id integer primary key autoincrement, year integer, month integer, day integer, title text, content text)";

        try {
            db.execSQL(sql);
        } catch(SQLException e) {
        }
    }

    private void removeDatabaseTable() {
        String sql = "DROP TABLE " + TABLE_NAME;

        try {
            db.execSQL(sql);
        } catch(SQLException e) {
        }
    }

    private boolean addDatabaseRow(int year, int month, int day, String strTitle, String strContent) {
        ContentValues values = new ContentValues();
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("title", strTitle);
        values.put("content", strContent);

        long newRowId = db.insert(TABLE_NAME, null, values);
        if (newRowId < 0)
            return false;
        return true;
    }

    private void deleteDatabaseRowByPosition(int position) {
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToPosition(position);
            int id = cursor.getInt(cursor.getColumnIndex("id"));

            String sqlDelete = "DELETE FROM " + TABLE_NAME + " WHERE id=" + id;
            db.execSQL(sqlDelete);
        }
    }

    private void updateCalendarSchedule() {
        scheduleListAdapter = new ArrayAdapter<String>(this, R.layout.textview_spinner);

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                int month = cursor.getInt(cursor.getColumnIndex("month")) + 1;
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                String strTitle = cursor.getString(cursor.getColumnIndex("title"));
                String strContent = cursor.getString(cursor.getColumnIndex("content"));

                scheduleListAdapter.add("[" + year + "년 " + month + "월 " + day + "일] " + strTitle + " (" + strContent + ")");
            } while (cursor.moveToNext());
        }

        scheduleList.setAdapter(scheduleListAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public  boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.newPost) {
            Intent intent = new Intent(this, Schedule.class);
            this.startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case 1: // 저장
                        String strTitle = intent.getStringExtra("title");
                        String strContent = intent.getStringExtra("content");
                        int year = intent.getIntExtra("year", curYear);
                        int month = intent.getIntExtra("month", curMonth);
                        int day = intent.getIntExtra("day", 1);

                        if (addDatabaseRow(year, month, day, strTitle, strContent))
                            updateCalendarSchedule();
                        break;
                    case 0: // 취소
                        break;
                }
                break;
        }
    }
}
