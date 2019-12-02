package com.example.testapplication;

public class YearList {
    protected  String date;
    protected  int total;
    protected  int inn_amt;
    protected  int out_amt;
    protected  int count;

    public  YearList(String date,int total, int inn_amt, int out_amt, int count) {
        this.date = date;
        this.total = total;
        this.inn_amt = inn_amt;
        this.out_amt = out_amt;
        this.count = count;
    }
    public String getDate(){
        return date;
    }
    public String getInn_amt(){
        return String.valueOf(inn_amt);
    }
    public String getOut_amt(){
        return String.valueOf(out_amt);
    }
    public String getCount(){
        return String.valueOf(count);
    }
    public String getTotal_amt(){
        return String.valueOf(total);
    }



}