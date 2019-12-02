package com.example.testapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends BaseAdapter {
    private List<Date> dateArray = new ArrayList();
    private Context mContext;
    private DateManager mDateManager;
    private LayoutInflater mLayoutInflater;
    static DBAdapter dbAdapter;
    private int monthResult;

    //カスタムセルを拡張したらここでWigetを定義
    private static class ViewHolder {
        public TextView dateText;
        //追加
        public TextView memoText;
    }

    public CalendarAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDateManager = new DateManager();
        dateArray = mDateManager.getDays();
    }

    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);
            //追加
            holder.memoText = convertView.findViewById(R.id.memoText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //セルのサイズを指定
        float dp = mContext.getResources().getDisplayMetrics().density;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(parent.getWidth()/7 - (int)dp, (parent.getHeight() - (int)dp * mDateManager.getWeeks() ) / mDateManager.getWeeks());
        convertView.setLayoutParams(params);

        //日付のみ表示させる
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));
        String memo = "";//ここでSQLiteからメモを取得　dateArray.get(position)を使う？
        //追加　→　メモを表示させる
        dbAdapter = new DBAdapter(parent.getContext());
        dbAdapter.open();
        String a;
        String b;
        Cursor c;

        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            //当月
            b = getTitle()+"." + holder.dateText.getText();
            c = dbAdapter.getMonthResult(getTitle());
        }else if(position < 7){
            //前月
            b = mDateManager.getPrevMonth()+"." + holder.dateText.getText();
            c = dbAdapter.getMonthResult(mDateManager.getPrevMonth());
        } else {
            //次月
            b = mDateManager.getNextMonth()+"." + holder.dateText.getText();
            c = dbAdapter.getMonthResult(mDateManager.getNextMonth());
        }

        if(c.moveToFirst()){
            do {
                a = c.getString(c.getColumnIndex(DBAdapter.COL_DATE));

                if (a.equals(b)) {
                    int result = c.getInt(c.getColumnIndex("SUM(OUT_AMT)")) - c.getInt(c.getColumnIndex("SUM(INN_AMT)"));
                    memo = String.valueOf(result);
                    if (result < 0) {
                        holder.memoText.setTextColor(Color.RED);
                    } else {
                        holder.memoText.setTextColor(Color.BLUE);
                    }
                }
            } while(c.moveToNext());
        }
        dbAdapter.close();

                holder.memoText.setText(memo);

        //当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            convertView.setBackgroundColor(Color.WHITE);
        }else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        //日曜日を赤、土曜日を青に
        int colorId;
        switch (mDateManager.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        return dateFormat.format(dateArray.get(position));
    }
    public  boolean isCurrentMonth(int position){
        if (mDateManager.isCurrentMonth(dateArray.get(position))){
            return true;
        }else {
            return false;
        }
    }

    public int getMonthResult(){
        dbAdapter = new DBAdapter(mContext);
        dbAdapter.open();
        Cursor c;
        c = dbAdapter.getMonthTotal(getTitle());
        if(c.moveToFirst()) {
            do {
                monthResult = c.getInt(c.getColumnIndex("SUM(OUT_AMT)")) - c.getInt(c.getColumnIndex("SUM(INN_AMT)"));
            } while (c.moveToNext());
        }
        dbAdapter.close();
        return monthResult;
    }

    //表示月を取得
    public String getTitle(){
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
            return format.format(mDateManager.mCalendar.getTime());

    }
    public String getNextMonth(){
        return mDateManager.getNextMonth();
    }

    public String getPrevMonth(){
        return  mDateManager.getPrevMonth();
    }

    //翌月表示
    public void nextMonth(){
        mDateManager.nextMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

    //前月表示
    public void prevMonth(){
        mDateManager.prevMonth();
        dateArray = mDateManager.getDays();
        this.notifyDataSetChanged();
    }

}
