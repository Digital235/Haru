package blurtic.haru;

/**
 * Created by DCB on 2016-10-11.
 */
public class PlanItem {
    int hour;
    int minute;
    int completeHour;
    int completeMinute;
    String name;

    public PlanItem(int h, int m, String n){
        hour=h;
        minute=m;
        name=n;
        completeMinute=-1;
        completeHour=-1;
    }
    public PlanItem(int h, int m, int ch, int cm, String n){
        hour=h;
        minute=m;
        completeMinute=ch;
        completeHour=cm;
        name=n;
    }
    public void modify(int h, int m, String n){
        hour=h;
        minute=m;
        name=n;
    }
    public void planComplete(int h, int m){
        completeHour=h;
        completeMinute=m;
    }
    public void planCompleteCancel(){
        completeHour=-1;
        completeMinute=-1;
    }
    public int getHour(){
        return hour;
    }
    public int getMinute(){
        return minute;
    }
    public int getCompleteHour(){
        return completeHour;
    }
    public int getCompleteMinute(){return completeMinute;}
    public String getName(){
        return name;
    }
}
