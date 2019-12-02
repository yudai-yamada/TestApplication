package com.example.testapplication;

public class ResultList {
    protected int kind;
    protected String  machine;
    protected  int inn_amt;
    protected  int out_amt;
    protected  int total_amt;
    protected  int count;

    public ResultList(int kind,String machine, int inn_amt, int out_amt, int total_amt,int count){
        this.kind = kind;
        this.machine = machine;
        this.inn_amt = inn_amt;
        this.out_amt = out_amt;
        this.total_amt = total_amt;
        this.count = count;

    }
    public String getKind(){
        if (kind == 0){
            return "パチンコ";
        }else{
            return "パチスロ";
        }
    }
    public String getMachine(){
        return machine;
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
        return String.valueOf(total_amt);
    }

}
