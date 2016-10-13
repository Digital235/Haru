package blurtic.haru.APISet.Weather.Class;

/**
 * Created by Kim W on 2016-06-13.
 */
public class WeatherToTime {
    public String date = "";
    public String time = "";
    public String type = "";
    public String value = "";

    String categoryName[] = {"POP","PTY","REH","SKY","UUU","VVV","VEC","WSD"};
    String categoryValue[] = {"","","","","","","","",}; // 최종 결과값임
    String categoryChangeVal[] = {"","","","","","","",""};
    boolean categoryCheck[] = {false,false,false,false,false,false,false,false,false,};
    //온도 날씨만
    public WeatherToTime(String date, String time, String type, String value) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.value = value;
    }
    public void ChangeMapping()
    {
        CurrentTimeInfo mInfo = new CurrentTimeInfo();
        for(int i = 0; i < categoryName.length; i++)
        {
            categoryChangeVal[i] = mInfo.CodeMapping(categoryName[i], categoryValue[i]);
        }
    }

    public boolean isFullAllData()
    {
        for(int i = 0; i < categoryValue.length; i++)
        {
            if(categoryCheck[i] == false)
                return false;
        }

        return true;
    }

    // 각 코드에 매핑
    // 한글로 변환은 currentTimeInfo에 기술
    public void HashValue(String category,String val)
    {
        for(int i = 0; i < categoryName.length; i++)
        {
            if(categoryName[i].equals(category)) {
                categoryValue[i] =  val;
                categoryCheck[i] = true;
                return ;
            }
        }

        return ;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setCategoryName(String[] categoryName) {
        this.categoryName = categoryName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }
}
