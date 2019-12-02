package com.example.testapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener{


    private TextView titleText;
    private TextView subTitleText;
    private TextView regTitleText;
    private TextView resultText;
    private TextView resultInnText;
    private TextView resultOutText;
    private Button prevButton, nextButton;
    private CalendarAdapter mCalendarAdapter;
    private GridView calendarGridView;
    private RadioGroup radioGroup;
    private Button machineResultButton;
    private Button yearResultButton;

    private TextView monthResult;

    static final String TAG = "SQLiteTest1";
    static final int MENUITEM_ID_DELETE = 1;
    ListView itemListView;
    ListView resultListView;
    EditText macineEditText;
    ListView yearListView;
    EditText inn_amtEditText;
    EditText out_amtEditText;
    EditText holeEditText;
    Button  saveButton;
    static DBAdapter dbAdapter;
    static NoteListAdapter listAdapter;
    static List<Note> noteList = new ArrayList<Note>();
    private int kind;

    static ResultListAdapter resultListAdapter;
    static List<ResultList> resultList = new ArrayList<ResultList>();

    static YearListAdapter yearListAdapter;
    static List<YearList> yearList = new ArrayList<YearList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreenMain(false);


    }
    @Override
    public void onBackPressed() {
        setScreenMain(true);

    }
        private void setScreenMain(boolean backFlg){
        setContentView(R.layout.activity_main);
        titleText = findViewById(R.id.titleText);
        monthResult = findViewById(R.id.monthResult);
        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.prevMonth();
                titleText.setText(mCalendarAdapter.getTitle());
                monthResult.setText(String.valueOf(mCalendarAdapter.getMonthResult()));
                if (mCalendarAdapter.getMonthResult() < 0){
                    monthResult.setTextColor(Color.RED);
                }else {
                    monthResult.setTextColor(Color.BLUE);
                }
            }
        });
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarAdapter.nextMonth();
                titleText.setText(mCalendarAdapter.getTitle());
                monthResult.setText(String.valueOf(mCalendarAdapter.getMonthResult()));
                if (mCalendarAdapter.getMonthResult() < 0){
                    monthResult.setTextColor(Color.RED);
                }else {
                    monthResult.setTextColor(Color.BLUE);
                }
            }
        });
        calendarGridView = findViewById(R.id.calendarGridView);
        if (backFlg == false) {
            mCalendarAdapter = new CalendarAdapter(this);
        }
        calendarGridView.setAdapter(mCalendarAdapter);
        calendarGridView.setOnItemClickListener(this);

        titleText.setText(mCalendarAdapter.getTitle());
        if (mCalendarAdapter.getMonthResult() < 0){
            monthResult.setTextColor(Color.RED);
        }else {
            monthResult.setTextColor(Color.BLUE);
        }
        monthResult.setText(String.valueOf(mCalendarAdapter.getMonthResult()));

        machineResultButton = findViewById(R.id.machine_button);
        machineResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMC(3);
            }
        });

        yearResultButton = findViewById(R.id.year_button);
        yearResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenYR();
            }
        });

        /*Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenSub();
            }
        });*/
    }


    private void setScreenSub(final int position, final View view){
        String str = (String) mCalendarAdapter.getItem(position);
        setContentView(R.layout.activity_sub);
        subTitleText = findViewById(R.id.subTitleText);
        //String str = String.valueOf(position);
        itemListView = (ListView)findViewById(R.id.itemListView);
        resultText = findViewById(R.id.resultText);
        resultInnText = findViewById(R.id.resultInnText);
        resultOutText = findViewById(R.id.resultOutText);
        dbAdapter = new DBAdapter(this);
        listAdapter = new NoteListAdapter();
        itemListView.setAdapter(listAdapter);

        if(mCalendarAdapter.isCurrentMonth(position)){
            loadNote(titleText.getText().toString() + "." + str);
            subTitleText.setText(titleText.getText().toString() + "." + str);
        } else if (position < 7){
            loadNote(mCalendarAdapter.getPrevMonth() + "." + str);
            subTitleText.setText(mCalendarAdapter.getPrevMonth() + "." + str);
        } else {
            loadNote(mCalendarAdapter.getNextMonth() + "." + str);
            subTitleText.setText(mCalendarAdapter.getNextMonth() + "." + str);
        }


        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int ListPosition, long id) {
                Note note = noteList.get(ListPosition);
                setScreenRegist(position, parent,note);
            }
        });

        dbAdapter.open();
        Cursor c = dbAdapter.getMonthResult((String) subTitleText.getText());
        if(c.moveToFirst()){
            do {
                String a = c.getString(c.getColumnIndex(DBAdapter.COL_DATE));
                String b = (String) subTitleText.getText();
                if (a.equals(b)) {
                    String result = String.valueOf(c.getInt(c.getColumnIndex("SUM(OUT_AMT)")) - c.getInt(c.getColumnIndex("SUM(INN_AMT)")));
                    resultText.setText("結果：" + result);
                    resultOutText.setText("総回収：" + String.valueOf(c.getInt(c.getColumnIndex("SUM(OUT_AMT)"))));
                    resultInnText.setText("総投資：" + String.valueOf(c.getInt(c.getColumnIndex("SUM(INN_AMT)"))));

                    if(Integer.valueOf(result) < 0){
                        resultText.setTextColor(Color.RED);
                    }else{
                        resultText.setTextColor(Color.BLUE);
                    }
                }
            } while(c.moveToNext());
        }
        dbAdapter.close();



        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMain(true);
            }
        });
        Button registButton = findViewById(R.id.regist_button);
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenRegist(position, view,null);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setScreenSub(position, view);
        // Intent intent = new Intent(getApplication(), TextActivity.class);
        //intent.putExtra("date", mCalendarAdapter.getItem(position).toString());
        //startActivity(intent);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.regist_button:
                saveItem(kind);
                break;
        }

        //ここに遷移するための処理を追加する
/*        switch (view.getId()){
            case R.id.hButton:
                Intent intent = new Intent(this,Main2Activity.class);
                startActivity(intent);
                break;

            case R.id.setButton:
                intent = new Intent(this,Main3Activity.class);
                startActivity(intent);
                break;
        }*/

        /*Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        */
    }

    //　　DB操作    //
    private void setScreenRegist(final int position, final View view, final Note note){

        dbAdapter = new DBAdapter(this);

        setContentView(R.layout.regist);
        macineEditText = (EditText)findViewById(R.id.macineEditText);
        inn_amtEditText = (EditText)findViewById(R.id.inn_amtEditText);
        out_amtEditText = (EditText)findViewById(R.id.out_amtEditText);
        holeEditText = (EditText)findViewById(R.id.holeEditText);
        regTitleText = findViewById(R.id.regTitleText);
        regTitleText.setText(subTitleText.getText().toString());
        radioGroup = findViewById(R.id.radioGroup);

        //数値のみ入力可
        inn_amtEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        out_amtEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        //ラジオボタンクリック処理
        //パチンコ:0、パチスロ:1をKINDに登録
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    switch (checkedId) {
                        case R.id.radioButton1:
                            kind = 0;
                            break;
                        case R.id.radioButton2:
                            kind = 1;
                            break;
                    }
                }
            }
        });

        //削除ボタン
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //アラートダイアログ表示
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("結果の削除")
                        .setMessage("この結果を削除します。本当にいいですか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbAdapter.open();
                                dbAdapter.deleteNote(String.valueOf(note.getId()));
                                dbAdapter.close();
                                setScreenSub(position, view);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });

        if(Objects.equals(note,null)){
            //登録処理
            Button registButton = findViewById(R.id.regist_button);
            registButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()){
                        case R.id.regist_button:
                            if(TextUtils.isEmpty(macineEditText.getText())||
                                    TextUtils.isEmpty(inn_amtEditText.getText()) ||
                                    TextUtils.isEmpty(out_amtEditText.getText()) ||
                                    TextUtils.isEmpty(holeEditText.getText())){
                                break;
                            } else {
                                saveItem(kind);
                                setScreenSub(position, view);
                            }

                            break;
                    }
                }
            });
            deleteButton.setVisibility(View.GONE);
        }else {

            //ＤＢ登録内容表示
            macineEditText.setText(note.getMacine());
            inn_amtEditText.setText(note.getInn_amt());
            out_amtEditText.setText(note.getOut_amt());
            holeEditText.setText(note.getHole());

            if (note.getKind() == 0){
                radioGroup.check(R.id.radioButton1);
            }else {
                radioGroup.check(R.id.radioButton2);
            }
            //更新処理
            Button registButton = findViewById(R.id.regist_button);
            registButton.setText("更新");
            registButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()){
                        case R.id.regist_button:
                            if(TextUtils.isEmpty(macineEditText.getText())||
                                    TextUtils.isEmpty(inn_amtEditText.getText()) ||
                                    TextUtils.isEmpty(out_amtEditText.getText()) ||
                                    TextUtils.isEmpty(holeEditText.getText())){
                                break;
                            } else {
                                updateItem(kind,note.getId());
                                setScreenSub(position, view);
                            }

                            break;
                    }
                }
            });
        }

        //戻るボタン
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenSub(position, view);
            }
        });



        //パチンコ機種検索ボタン
        Button machineButton = findViewById(R.id.PCmachineButton);
        machineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editView = new EditText(MainActivity.this);
                dbAdapter.open();
                Cursor Cr = dbAdapter.getPCMachine();
                int i = Cr.getCount();

                if(Cr.getCount() > 0){

                    final String[] items = new String[Cr.getCount()];

                    if(Cr.moveToFirst()) {
                        do {
                            int j = Cr.getPosition();
                            items[Cr.getPosition()] = Cr.getString(Cr.getColumnIndex(DBAdapter.COL_MACINE));
                        } while (Cr.moveToNext());
                    }

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("テキスト入力ダイアログ")
                            .setItems(items, new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // whichには選択したリスト項目の順番が入っているので、それを使用して値を取得
                                    String selectedVal = items[which];
                                    radioGroup.check(R.id.radioButton1);

                                    // MainActivityのインスタンスを取得
                                    macineEditText.setText(selectedVal);
                                }
                            })
                            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }

                dbAdapter.close();


            }
        });

        //パチスロ機種検索ボタン
        Button SLmachineButton = findViewById(R.id.SLmachineButton);
        SLmachineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editView = new EditText(MainActivity.this);
                dbAdapter.open();
                Cursor Cr = dbAdapter.getSLMachine();
                int i = Cr.getCount();

                if(Cr.getCount() > 0){

                    final String[] items = new String[Cr.getCount()];

                    if(Cr.moveToFirst()) {
                        do {
                            int j = Cr.getPosition();
                            items[Cr.getPosition()] = Cr.getString(Cr.getColumnIndex(DBAdapter.COL_MACINE));
                        } while (Cr.moveToNext());
                    }

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("テキスト入力ダイアログ")
                            .setItems(items, new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // whichには選択したリスト項目の順番が入っているので、それを使用して値を取得
                                    String selectedVal = items[which];
                                    radioGroup.check(R.id.radioButton2);

                                    // MainActivityのインスタンスを取得
                                    macineEditText.setText(selectedVal);
                                }
                            })
                            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }

                dbAdapter.close();


            }
        });

    }

    //機種別結果画面表示
    //type:0 パチンコ、:1パチスロ、:other両方
    private  void  setScreenMC(int type){
        setContentView(R.layout.activity_machine);
        resultListView = (ListView)findViewById(R.id.machineListView);
        resultText = findViewById(R.id.resultText);
        resultInnText = findViewById(R.id.resultInnText);
        resultOutText = findViewById(R.id.resultOutText);
        dbAdapter = new DBAdapter(this);
        resultListAdapter = new ResultListAdapter();
        resultListView.setAdapter(resultListAdapter);

        loadResult(type);

/*        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int ListPosition, long id) {
                Note note = noteList.get(ListPosition);
                setScreenRegist(position, parent,note);
            }
        });*/

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMain(true);
            }
        });
        Button ALLButton = findViewById(R.id.all_total_button);
        ALLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMC(3);
            }
        });
        Button PCButton = findViewById(R.id.PC_total_button);
        PCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMC(0);
            }
        });
        Button SLButton = findViewById(R.id.SL_total_button);
        SLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMC(1);
            }
        });

    }
    //機種別結果読込
    protected void loadResult(int kind){
        resultList.clear();

        // Read
        dbAdapter.open();
        Cursor c = dbAdapter.getResultByMachine(kind);

        startManagingCursor(c);

        if(c.moveToFirst()){
            do {

                ResultList result = new ResultList(
                        c.getInt(c.getColumnIndex(DBAdapter.COL_KIND)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_MACINE)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_INN_AMT)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_OUT_AMT)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_TOTAL)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_COUNT))
                );
                resultList.add(result);
            } while(c.moveToNext());
        }

        stopManagingCursor(c);
        dbAdapter.close();

        resultListAdapter.notifyDataSetChanged();
    }

    //年別結果画面表示
    private  void  setScreenYR(){
        setContentView(R.layout.activity_year);
        yearListView = (ListView)findViewById(R.id.yearListView);

        dbAdapter = new DBAdapter(this);
        yearListAdapter = new YearListAdapter();
        yearListView.setAdapter(yearListAdapter);

        loadYear();

/*        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int ListPosition, long id) {
                Note note = noteList.get(ListPosition);
                setScreenRegist(position, parent,note);
            }
        });*/

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreenMain(true);
            }
        });

    }

    //年別結果読込
    protected void loadYear(){
        yearList.clear();

        // Read
        dbAdapter.open();
        Cursor c = dbAdapter.getYearResult();

        startManagingCursor(c);

        if(c.moveToFirst()){
            do {

                YearList year = new YearList(
                        c.getString(c.getColumnIndex(DBAdapter.COL_YEAR)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_TOTAL)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_INN_AMT)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_OUT_AMT)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_COUNT))
                );
                yearList.add(year);
            } while(c.moveToNext());
        }

        stopManagingCursor(c);
        dbAdapter.close();

        yearListAdapter.notifyDataSetChanged();
    }

    //日別結果読込
    protected void loadNote(String str){
        noteList.clear();

        // Read
        dbAdapter.open();
        Cursor c = dbAdapter.getDayNotes(str);

        startManagingCursor(c);

        if(c.moveToFirst()){
            do {
                Cursor C2 = dbAdapter.getMachineName(c.getInt(c.getColumnIndex(DBAdapter.COL_MACINE)));
                C2.moveToFirst();
                Note note = new Note(
                        c.getInt(c.getColumnIndex(DBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_DATE)),
                        C2.getString(C2.getColumnIndex(DBAdapter.COL_MACINE)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_INN_AMT)),
                        c.getInt(c.getColumnIndex(DBAdapter.COL_OUT_AMT)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_HOLE)),
                        c.getString(c.getColumnIndex(DBAdapter.COL_LASTUPDATE)),
                        C2.getInt(C2.getColumnIndex(DBAdapter.COL_KIND))
                );
                noteList.add(note);
            } while(c.moveToNext());
        }

        stopManagingCursor(c);
        dbAdapter.close();

        listAdapter.notifyDataSetChanged();
    }


    protected void saveItem(int kind){
        dbAdapter.open();
        dbAdapter.saveResult(
                regTitleText.getText().toString(),                        //日付
                macineEditText.getText().toString(),                      //機種名
                Integer.parseInt(inn_amtEditText.getText().toString()),   //投資
                Integer.parseInt(out_amtEditText.getText().toString()),   //回収
                holeEditText.getText().toString(),                         //店名
                kind                                                         //種類
        );
        dbAdapter.close();
        //noteEditText.setText("");
        //loadNote();
    }

    protected  void updateItem(int kind,int id){
        dbAdapter.open();
        dbAdapter.updateResult(
                regTitleText.getText().toString(),                        //日付
                macineEditText.getText().toString(),                      //機種名
                Integer.parseInt(inn_amtEditText.getText().toString()),   //投資
                Integer.parseInt(out_amtEditText.getText().toString()),   //回収
                holeEditText.getText().toString(),                         //店名
                kind,                                                        //種類
                id                                                           //ID
        );
        dbAdapter.close();
    }


    //日別結果表示用クラス
    private class NoteListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public Object getItem(int position) {
            return noteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView macineTextView;
            TextView holeTextView;
            TextView resultTextView;
            TextView inn_amtTextView;
            TextView out_amtTextView;
            View v = convertView;
            if(v==null){
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row, null);
            }
            Note note = (Note)getItem(position);
            if(note != null){
                macineTextView = (TextView)v.findViewById(R.id.macineTextView);
                holeTextView = (TextView)v.findViewById(R.id.holeTextView);
                resultTextView = (TextView)v.findViewById(R.id.resultTextView);
                inn_amtTextView = (TextView)v.findViewById(R.id.inn_amtTextView);
                out_amtTextView = (TextView)v.findViewById(R.id.out_amtTextView);
                macineTextView.setText(note.getMacine());
                holeTextView.setText(note.getHole());
                resultTextView.setText(note.getResult());
                if (Integer.valueOf(note.getResult()) < 0){
                    resultTextView.setTextColor(Color.RED);
                } else {
                    resultTextView.setTextColor(Color.BLUE);
                }

                inn_amtTextView.setText(note.getInn_amt());
                out_amtTextView.setText(note.getOut_amt());
                v.setTag(R.id.macineText, note);
            }
            return v;
        }

    }
    //機種別結果表示用クラス
    public class ResultListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView kindTextView;
            TextView machineTextView;
            TextView resultTextView;
            TextView inn_amtTextView;
            TextView out_amtTextView;
            TextView countTextView;
            View v = convertView;
            if(v==null){
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_machine, null);
            }
            ResultList result = (ResultList) getItem(position);
            if(result != null){
                kindTextView = (TextView)v.findViewById(R.id.kindTextView);
                machineTextView = (TextView)v.findViewById(R.id.machineTextView);
                resultTextView = (TextView)v.findViewById(R.id.resultTextView);
                inn_amtTextView = (TextView)v.findViewById(R.id.inn_amtTextView);
                out_amtTextView = (TextView)v.findViewById(R.id.out_amtTextView);
                countTextView = (TextView)v.findViewById(R.id.countTextView);

                kindTextView.setText(result.getKind());
                machineTextView.setText(result.getMachine());
                resultTextView.setText(result.getTotal_amt());
                if (Integer.valueOf(Integer.valueOf(result.getTotal_amt())) < 0){
                    resultTextView.setTextColor(Color.RED);
                } else {
                    resultTextView.setTextColor(Color.BLUE);
                }
                inn_amtTextView.setText(result.getInn_amt());
                out_amtTextView.setText(result.getOut_amt());
                countTextView.setText(result.getCount());
                v.setTag(R.id.macineText, result);
            }
            return v;
        }

    }

    //年別結果表示用クラス
    public class YearListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return yearList.size();
        }

        @Override
        public Object getItem(int position) {
            return yearList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView yearTextView;
            TextView resultTextView;
            TextView inn_amtTextView;
            TextView out_amtTextView;
            TextView countTextView;
            View v = convertView;
            if(v==null){
                LayoutInflater inflater =
                        (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_year, null);
            }
            YearList year = (YearList) getItem(position);
            if(year != null){
                yearTextView = (TextView)v.findViewById(R.id.yearTextView);
                resultTextView = (TextView)v.findViewById(R.id.resultTextView);
                inn_amtTextView = (TextView)v.findViewById(R.id.inn_amtTextView);
                out_amtTextView = (TextView)v.findViewById(R.id.out_amtTextView);
                countTextView = (TextView)v.findViewById(R.id.countTextView);

                yearTextView.setText(year.getDate());
                resultTextView.setText(year.getTotal_amt());
                if (Integer.valueOf(Integer.valueOf(year.getTotal_amt())) < 0){
                    resultTextView.setTextColor(Color.RED);
                } else {
                    resultTextView.setTextColor(Color.BLUE);
                }
                inn_amtTextView.setText(year.getInn_amt());
                out_amtTextView.setText(year.getOut_amt());
                countTextView.setText(year.getCount());
                v.setTag(R.id.macineText, year);
            }
            return v;
        }

    }

}





