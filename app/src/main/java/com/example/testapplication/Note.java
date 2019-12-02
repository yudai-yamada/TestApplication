package com.example.testapplication;

public class Note {
    protected int id;
    protected String date;
    protected String macine;
    protected int inn_amt;
    protected int out_amt;
    protected int kind;
    protected String hole;
    protected String lastupdate;

    public Note(int id, String date, String macine, int inn_amt, int out_amt, String hole, String lastupdate,int kind){
        this.id = id;
        this.date = date;
        this.macine = macine;
        this.inn_amt = inn_amt;
        this.out_amt = out_amt;
        this.kind = kind;
        this.hole = hole;
        this.lastupdate = lastupdate;
    }

    public String getDate(){
        return date;
    }
    public String getMacine(){
        return macine;
    }
    public String getInn_amt(){
        return String.valueOf(inn_amt);
    }
    public String getOut_amt(){
        return String.valueOf(out_amt);
    }
    public String getHole(){
        return hole;
    }
    public String getResult(){
        return String.valueOf(out_amt - inn_amt);
    }
    public int getKind(){ return kind; }

    public String getLastupdate(){
        return lastupdate;
    }

    public int getId(){
        return id;
    }

}